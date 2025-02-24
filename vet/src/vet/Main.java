package vet;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService();
        ClientService clientService = new ClientService();
        PetService petService = new PetService();
        AppointmentService appointmentService = new AppointmentService();
        AvailabilityService availabilityService = new AvailabilityService();
        Scanner scanner = new Scanner(System.in);

        try {
            userService.createUser("admin", PasswordUtils.hashPassword("admin123"), "admin");
            System.out.println("Usuário administrador criado com sucesso.");
        } catch (SQLException e) {
            System.out.println("Aviso: O usuário administrador já existe.");
        }

        while (true) {
            try {
                System.out.println("\n=== Sistema de Clínica Veterinária ===");
                System.out.println("1. Cadastrar Cliente");
                System.out.println("2. Cadastrar Pet");
                System.out.println("3. Consultar Agendamentos");
                System.out.println("4. Verificar Status de Consulta");
                System.out.println("5. Realizar Consulta");
                System.out.println("6. Sair");
                System.out.print("Escolha uma opção: ");

                int opcao = scanner.nextInt();
                scanner.nextLine(); // Consumir nova linha

                switch (opcao) {
                    case 1:
                        cadastrarCliente(scanner, clientService);
                        break;
                    case 2:
                        cadastrarPet(scanner, clientService, petService, availabilityService, appointmentService);
                        break;
                    case 3:
                        consultarAgendamentos(scanner, clientService, appointmentService);
                        break;
                    case 4:
                        verificarStatus(scanner, clientService, appointmentService);
                        break;
                    case 5:
                        realizarConsulta(scanner, clientService, petService, availabilityService, appointmentService);
                        break;
                    case 6:
                        System.out.println("Encerrando o sistema...");
                        scanner.close();
                        System.exit(0);
                    default:
                        System.out.println("Opção inválida!");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
                scanner.nextLine(); // Limpar buffer
            }
        }
    }

    private static void realizarConsulta(Scanner scanner, ClientService clientService, PetService petService,
            AvailabilityService availabilityService, AppointmentService appointmentService) throws SQLException {
        System.out.println("\n=== Realizar Consulta ===");
        System.out.print("Digite o ID da consulta: ");
        int appointmentId = scanner.nextInt();
        scanner.nextLine(); // Consumir nova linha
        
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        if (appointment == null) {
            System.out.println("Consulta não encontrada!");
            return;
        }
        
        Pet pet = petService.getPetById(appointment.getPetId());
        if (pet == null) {
            System.out.println("Pet não encontrado!");
            return;
        }
        
        Client client = clientService.getClientById(pet.getClientId());
        if (client == null) {
            System.out.println("Cliente não encontrado!");
            return;
        }
        
        System.out.println("Detalhes da Consulta:");
        System.out.println("Cliente: " + client.getName());
        System.out.println("Pet: " + pet.getName());
        System.out.println("Serviço: " + appointment.getService());
        System.out.println("Status atual: " + appointment.getStatus());
        
        System.out.print("Digite as observações da consulta: ");
        String notes = scanner.nextLine();
        
        System.out.println("Selecione o novo status:");
        System.out.println("1. Em andamento");
        System.out.println("2. Concluído");
        System.out.println("3. Cancelado");
        
        int statusOption = scanner.nextInt();
        scanner.nextLine(); // Consumir nova linha
        
        String newStatus;
        switch (statusOption) {
            case 1:
                newStatus = "em_andamento";
                break;
            case 2:
                newStatus = "concluido";
                break;
            case 3:
                newStatus = "cancelado";
                break;
            default:
                System.out.println("Opção inválida!");
                return;
        }
        
        appointment.setStatus(newStatus);
        appointment.setNotes(notes);
        appointmentService.updateAppointment(appointment);
        
        System.out.println("Consulta atualizada com sucesso!");
        
        // Enviar email de notificação (simulado)
        if (newStatus.equals("concluido")) {
            System.out.println("Enviando email de confirmação para " + client.getEmail());
            // EmailService.sendEmail(client.getEmail(), "Consulta Concluída", 
            //     "Prezado(a) " + client.getName() + ",\n\nSua consulta para " + pet.getName() + " foi concluída.\n\nObservações: " + notes);
        }
    }

    private static void cadastrarCliente(Scanner scanner, ClientService clientService) {
        System.out.println("\n=== Cadastro de Cliente ===");
        Client client = new Client();

        System.out.print("Nome: ");
        client.setName(scanner.nextLine());
        
        System.out.print("Email: ");
        client.setEmail(scanner.nextLine());
        
        System.out.print("Telefone: ");
        client.setPhone(scanner.nextLine());
        
        System.out.print("Endereço: ");
        client.setAddress(scanner.nextLine());

        try {
            clientService.addClient(client);
            System.out.println("Cliente cadastrado com sucesso! ID: " + client.getClientId());
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar cliente: " + e.getMessage());
        }
    }

    private static void cadastrarPet(Scanner scanner, ClientService clientService, PetService petService,
            AvailabilityService availabilityService, AppointmentService appointmentService) {
        System.out.println("\n=== Cadastro de Pet ===");

        try {
            System.out.print("ID do Cliente: ");
            int clientId = scanner.nextInt();
            scanner.nextLine();

            Client client = clientService.getClientById(clientId);
            if (client == null) {
                System.out.println("Cliente não encontrado!");
                return;
            }

            Pet pet = new Pet();
            pet.setClientId(clientId);

            System.out.print("Nome do Pet: ");
            pet.setName(scanner.nextLine());

            System.out.print("Espécie: ");
            pet.setSpecies(scanner.nextLine());

            System.out.print("Raça: ");
            pet.setBreed(scanner.nextLine());

            System.out.print("Data de Nascimento (dd/MM/yyyy): ");
            String birthDateStr = scanner.nextLine();
            Date birthDate = parseDate1(birthDateStr);
            
            if (birthDate == null) {
                System.out.println("Data inválida!");
                return;
            }
            pet.setBirthDate(birthDate);

            int petId = petService.addPet(pet);
            if (petId != -1) {
                System.out.println("Pet cadastrado com sucesso! ID: " + petId);
                System.out.println("Deseja agendar uma consulta para o pet? (S/N)");
                if (scanner.nextLine().equalsIgnoreCase("S")) {
                    solicitarTipoServico(scanner, petId, availabilityService, appointmentService);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar pet: " + e.getMessage());
        }
    }

    private static Date parseDate1(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date date = sdf.parse(dateStr);
            return new Date(date.getTime());
        } catch (ParseException e) {
            System.out.println("Erro ao converter data: " + e.getMessage());
            return null;
        }
    }

    private static void solicitarTipoServico(Scanner scanner, int petId, AvailabilityService availabilityService, AppointmentService appointmentService) {
        System.out.println("Selecione o tipo de serviço:");
        System.out.println("1. Banho");
        System.out.println("2. Tosa");
        System.out.println("3. Vacina");
        System.out.println("4. Consulta");

        int tipoServico = scanner.nextInt();
        scanner.nextLine(); // Consumir a nova linha

        String service;
        switch (tipoServico) {
            case 1:
                service = "Banho";
                break;
            case 2:
                service = "Tosa";
                break;
            case 3:
                service = "Vacina";
                break;
            case 4:
                service = "Consulta";
                break;
            default:
                System.out.println("Opção inválida. Tente novamente.");
                return;
        }

        agendarConsultaComServico(scanner, petId, service, availabilityService, appointmentService);
    }

    private static void agendarConsultaComServico(Scanner scanner, int petId, String service, AvailabilityService availabilityService, AppointmentService appointmentService) {
        System.out.println("Seleção de Data e Horário para Agendamento:");
        System.out.print("Data (dd/MM/yyyy): ");
        String dateStr = scanner.nextLine();
        Date date = parseDate1(dateStr);

        try {
            // Gerar horários disponíveis automaticamente
            generateAvailability(date);

            // Listar horários disponíveis
            List<Availability> availableSlots = availabilityService.getAvailableSlots(date);
            if (availableSlots.isEmpty()) {
                System.out.println("Nenhum horário disponível para a data selecionada.");
            } else {
                System.out.println("Horários disponíveis:");
                for (Availability slot : availableSlots) {
                    System.out.println("ID do Horário: " + slot.getSlotId() + " | Início: " + slot.getStartTime() + " | Término: " + slot.getEndTime());
                }

                System.out.print("Selecione o ID do horário desejado: ");
                int slotId = scanner.nextInt();
                scanner.nextLine(); // Consumir nova linha

                // Agendamento de consulta
                Availability selectedSlot = availableSlots.stream()
                        .filter(slot -> slot.getSlotId() == slotId)
                        .findFirst()
                        .orElse(null);

                if (selectedSlot != null) {
                    availabilityService.bookSlot(slotId);
                    Appointment appointment = new Appointment();
                    appointment.setPetId(petId);
                    appointment.setService(service);
                    appointment.setStartTime(selectedSlot.getStartTime());
                    appointment.setEndTime(selectedSlot.getEndTime());
                    appointment.setStatus("pendente");
                    appointment.setNotes("Consulta de rotina");

                    appointmentService.scheduleAppointment(appointment);
                    System.out.println("Agendamento realizado com sucesso.");
                } else {
                    System.out.println("Horário selecionado não está disponível.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao agendar consulta: " + e.getMessage());
        }
    }

    private static void consultarAgendamentos(Scanner scanner, ClientService clientService, AppointmentService appointmentService) {
        System.out.print("Digite seu e-mail: ");
        String email = scanner.nextLine();
        System.out.print("Digite seu telefone: ");
        String phone = scanner.nextLine();
        
        try {
            int clientId = clientService.getClientId(email, phone);
            if (clientId != -1) {
                List<Appointment> appointments = appointmentService.getAppointmentsByClientId(clientId);
                if (appointments.isEmpty()) {
                    System.out.println("Nenhum agendamento encontrado.");
                } else {
                    System.out.println("Agendamentos:");
                    for (Appointment appointment : appointments) {
                        System.out.println("ID da Consulta: " + appointment.getAppointmentId() + 
                                           " | Serviço: " + appointment.getService() + 
                                           " | Início: " + appointment.getStartTime() + 
                                           " | Status: " + appointment.getStatus());
                    }
                }
            } else {
                System.out.println("Cliente não encontrado.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar agendamentos: " + e.getMessage());
        }
    }

    private static void verificarStatus(Scanner scanner, ClientService clientService, AppointmentService appointmentService) {
        System.out.print("Digite seu e-mail: ");
        String email = scanner.nextLine();
        System.out.print("Digite seu telefone: ");
        String phone = scanner.nextLine();
        
        try {
            int clientId = clientService.getClientId(email, phone);
            if (clientId != -1) {
                System.out.print("Digite a data da consulta (dd/MM/yyyy): ");
                String dateStr = scanner.nextLine();
                Date date = parseDate(dateStr);

                List<Appointment> appointments = appointmentService.getAppointmentsByClientId(clientId);
                if (appointments.isEmpty()) {
                    System.out.println("Nenhum agendamento encontrado.");
                } else {
                    boolean found = false;
                    for (Appointment appointment : appointments) {
                        if (appointment.getStartTime().toLocalDateTime().toLocalDate().equals(date.toLocalDate())) {
                            System.out.println("ID da Consulta: " + appointment.getAppointmentId() + 
                                               " | Serviço: " + appointment.getService() + 
                                               " | Início: " + appointment.getStartTime() + 
                                               " | Status: " + appointment.getStatus());
                            found = true;
                        }
                    }
                    if (!found) {
                        System.out.println("Nenhum agendamento encontrado para a data selecionada.");
                    }
                }
            } else {
                System.out.println("Cliente não encontrado.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar status: " + e.getMessage());
        }
    }

    private static Date parseDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date date = sdf.parse(dateStr);
            return new Date(date.getTime());
        } catch (ParseException e) {
            System.out.println("Erro ao converter data: " + e.getMessage());
            return null;
        }
    }

    private static void generateAvailability(Date startDate) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL generate_availability(?, ?)}")) {

            stmt.setDate(1, startDate);
            stmt.setDate(2, Date.valueOf(startDate.toLocalDate().plusDays(30))); // Gera disponibilidade por 30 dias a partir da data fornecida

            stmt.execute();
        }
    }
}