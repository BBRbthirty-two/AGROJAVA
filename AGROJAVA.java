import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

public class AGROJAVA {

    static Scanner scanner = new Scanner(System.in).useLocale(Locale.US);
    static double[] diasChuva = new double[7];
    static String[] nomesDias = {
        "Segunda-feira", "Terça-feira", "Quarta-feira",
        "Quinta-feira", "Sexta-feira", "Sábado", "Domingo"
    };
    static int[][] matrizUmidade = new int[4][4];
    static boolean chuvasCadastradas = false;
    static boolean talhoesCadastrados = false;

    static final String ARQUIVO_CHUVAS = "historico_chuvas.csv";
    static final String ARQUIVO_TALHOES = "mapa_umidade.csv";
    static final String ARQUIVO_RELATORIO = "relatorio_fazenda.txt";

    public static void main(String[] args) {
        menuPrincipal();
        scanner.close();
    }

    public static void menuPrincipal() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n===== SISTEMA INTEGRADO DO AGRONEGÓCIO (AGROJAVA) =====");
            System.out.println("1) Cadastrar Dados (Chuvas da Semana e Umidade dos Talhões)");
            System.out.println("2) Exibir Mapa do Campo");
            System.out.println("3) Relatório de Alertas de Irrigação");
            System.out.println("4) Consultar Histórico de Chuvas");
            System.out.println("5) Identificar Semana Mais Chuvosa");
            System.out.println("6) Dashboard Climático");
            System.out.println("7) Relatório Gerencial da Fazenda");
            System.out.println("8) Exportar Relatório da Fazenda (.txt)");
            System.out.println("0) Sair");
            System.out.print("Escolha uma opção: ");

            if (scanner.hasNextInt()) {
                opcao = scanner.nextInt();
                scanner.nextLine();
            } else {
                scanner.nextLine();
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    cadastrarChuvas();
                    cadastrarTalhoes();
                    break;
                case 2:
                    exibirMapaCampo();
                    break;
                case 3:
                    gerarRelatorioIrrigacao();
                    break;
                case 4:
                    consultarHistoricoChuvas();
                    break;
                case 5:
                    identificarSemanaMaisChuvosa();
                    break;
                case 6:
                    gerarDashboardClimatico();
                    break;
                case 7:
                    gerarRelatorioGerencial();
                    break;
                case 8:
                    exportarRelatorio();
                    break;
                case 0:
                    System.out.println("Encerrando o sistema AgroJava...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    public static void cadastrarChuvas() {
        System.out.println("\n--- CADASTRO DE CHUVAS DA SEMANA ---");
        System.out.print("Informe a data da semana (dd/MM/yyyy): ");
        String dataSemana = scanner.nextLine().trim();

        double total = 0;
        int indiceMaior = 0;

        for (int i = 0; i < diasChuva.length; i++) {
            System.out.print("Chuva de " + nomesDias[i] + " (mm): ");
            diasChuva[i] = scanner.nextDouble();
            total += diasChuva[i];
            if (diasChuva[i] > diasChuva[indiceMaior]) {
                indiceMaior = i;
            }
        }
        scanner.nextLine();

        double media = total / diasChuva.length;
        chuvasCadastradas = true;

        System.out.printf(Locale.US, "Média semanal de pluviosidade: %.1f mm%n", media);
        System.out.printf(Locale.US, "Total acumulado na semana: %.1f mm%n", total);
        System.out.printf(Locale.US, "Dia com maior índice de chuva: %s (%.1f mm)%n",
                nomesDias[indiceMaior], diasChuva[indiceMaior]);

        salvarHistoricoChuvas(dataSemana, diasChuva, media, total);
    }

    public static void salvarHistoricoChuvas(String data, double[] chuvas, double media, double total) {
        File arquivo = new File(ARQUIVO_CHUVAS);
        boolean existe = arquivo.exists() && arquivo.length() > 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo, true))) {
            if (!existe) {
                writer.write("data,seg,ter,qua,qui,sex,sab,dom,media,total");
                writer.newLine();
            }
            StringBuilder linha = new StringBuilder();
            linha.append(data).append(",");
            for (int i = 0; i < chuvas.length; i++) {
                linha.append(String.format(Locale.US, "%.1f", chuvas[i])).append(",");
            }
            linha.append(String.format(Locale.US, "%.1f", media)).append(",");
            linha.append(String.format(Locale.US, "%.1f", total));

            writer.write(linha.toString());
            writer.newLine();
            System.out.println("Histórico de chuvas salvo em " + ARQUIVO_CHUVAS);
        } catch (IOException e) {
            System.out.println("Erro ao salvar histórico de chuvas: " + e.getMessage());
        }
    }

    public static void consultarHistoricoChuvas() {
        System.out.println("\n===== HISTÓRICO DE CHUVAS =====");
        File arquivo = new File(ARQUIVO_CHUVAS);
        if (!arquivo.exists()) {
            System.out.println("Nenhum histórico encontrado.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha = reader.readLine();
            boolean possuiRegistros = false;

            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue;
                }
                String[] partes = linha.split(",");
                if (partes.length >= 10) {
                    possuiRegistros = true;
                    System.out.println("Semana: " + partes[0]);
                    System.out.println("Média: " + partes[8] + " mm");
                    System.out.println("Total: " + partes[9] + " mm");
                    System.out.println("-------------------------------");
                }
            }

            if (!possuiRegistros) {
                System.out.println("Nenhum registro cadastrado no arquivo.");
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler histórico de chuvas: " + e.getMessage());
        }
    }

    public static void identificarSemanaMaisChuvosa() {
        System.out.println("\n===== SEMANA MAIS CHUVOSA =====");
        File arquivo = new File(ARQUIVO_CHUVAS);
        if (!arquivo.exists()) {
            System.out.println("Nenhum histórico encontrado.");
            return;
        }

        String semanaMaisChuvosa = "";
        double maiorTotal = -1;

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            reader.readLine();
            String linha;

            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue;
                }
                String[] partes = linha.split(",");
                if (partes.length >= 10) {
                    double totalAtual = Double.parseDouble(partes[9]);
                    if (totalAtual > maiorTotal) {
                        maiorTotal = totalAtual;
                        semanaMaisChuvosa = partes[0];
                    }
                }
            }

            if (maiorTotal >= 0) {
                System.out.println("Semana mais chuvosa: " + semanaMaisChuvosa);
                System.out.printf(Locale.US, "Total: %.1f mm%n", maiorTotal);
            } else {
                System.out.println("Nenhum registro válido encontrado.");
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Erro ao processar semana mais chuvosa: " + e.getMessage());
        }
    }

    public static void gerarDashboardClimatico() {
        System.out.println("\n===== DASHBOARD =====");
        File arquivo = new File(ARQUIVO_CHUVAS);
        if (!arquivo.exists()) {
            System.out.println("Nenhum histórico encontrado.");
            return;
        }

        int qtdSemanas = 0;
        double volumeAcumulado = 0;
        double maiorChuva = -1;
        double menorChuva = Double.MAX_VALUE;
        String semanaMaior = "";
        String semanaMenor = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            reader.readLine();
            String linha;

            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue;
                }
                String[] partes = linha.split(",");
                if (partes.length >= 10) {
                    String data = partes[0];
                    double totalSemana = Double.parseDouble(partes[9]);

                    qtdSemanas++;
                    volumeAcumulado += totalSemana;

                    if (totalSemana > maiorChuva) {
                        maiorChuva = totalSemana;
                        semanaMaior = data;
                    }
                    if (totalSemana < menorChuva) {
                        menorChuva = totalSemana;
                        semanaMenor = data;
                    }
                }
            }

            if (qtdSemanas > 0) {
                double mediaGeral = volumeAcumulado / qtdSemanas;
                System.out.println("Semanas cadastradas: " + qtdSemanas);
                System.out.printf(Locale.US, "Volume acumulado: %.1f mm%n", volumeAcumulado);
                System.out.printf(Locale.US, "Maior chuva: %.1f mm (%s)%n", maiorChuva, semanaMaior);
                System.out.printf(Locale.US, "Menor chuva: %.1f mm (%s)%n", menorChuva, semanaMenor);
                System.out.printf(Locale.US, "Média geral: %.1f mm%n", mediaGeral);
            } else {
                System.out.println("Nenhum registro cadastrado.");
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Erro ao gerar dashboard climático: " + e.getMessage());
        }
    }

    public static String obterCodigoTalhao(int linha, int coluna) {
        char letraLinha = (char) ('A' + linha);
        return letraLinha + String.valueOf(coluna + 1);
    }

    public static String classificarTalhao(int umidade) {
        if (umidade < 20) {
            return "Crítico";
        } else if (umidade <= 30) {
            return "Atenção";
        } else {
            return "Adequado";
        }
    }

    public static void cadastrarTalhoes() {
        System.out.println("\n--- MAPEAMENTO DE UMIDADE DO SOLO (4x4) ---");
        for (int i = 0; i < matrizUmidade.length; i++) {
            for (int j = 0; j < matrizUmidade[i].length; j++) {
                String codigo = obterCodigoTalhao(i, j);
                System.out.print("Informe a umidade (%) do talhão " + codigo + " [" + i + "][" + j + "]: ");
                int umidade = scanner.nextInt();
                matrizUmidade[i][j] = umidade;

                if (umidade < 30) {
                    System.out.println("ALERTA: O talhão " + codigo + " (" + umidade + "%) necessita de irrigação.");
                }
            }
        }
        scanner.nextLine();
        talhoesCadastrados = true;
        salvarMapaTalhoes();
    }

    public static void salvarMapaTalhoes() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_TALHOES))) {
            writer.write("talhao,umidade,status");
            writer.newLine();

            for (int i = 0; i < matrizUmidade.length; i++) {
                for (int j = 0; j < matrizUmidade[i].length; j++) {
                    String codigo = obterCodigoTalhao(i, j);
                    int umidade = matrizUmidade[i][j];
                    String status = classificarTalhao(umidade);
                    writer.write(codigo + "," + umidade + "," + status);
                    writer.newLine();
                }
            }
            System.out.println("Mapa de umidade salvo em " + ARQUIVO_TALHOES);
        } catch (IOException e) {
            System.out.println("Erro ao salvar mapa de umidade: " + e.getMessage());
        }
    }

    public static void exibirMapaCampo() {
        System.out.println("\n===== MAPA DO CAMPO (UMIDADE %) =====");
        if (!talhoesCadastrados && !carregarMapaDoArquivo()) {
            System.out.println("Nenhum dado de talhões cadastrado. Use a opção 1 primeiro.");
            return;
        }

        System.out.println("      Col 1   Col 2   Col 3   Col 4");
        for (int i = 0; i < matrizUmidade.length; i++) {
            char linhaLetra = (char) ('A' + i);
            System.out.print("[" + linhaLetra + "] ");
            for (int j = 0; j < matrizUmidade[i].length; j++) {
                System.out.printf("%6d%% ", matrizUmidade[i][j]);
            }
            System.out.println();
        }
    }

    public static void gerarRelatorioIrrigacao() {
        System.out.println("\n===== ALERTAS =====");
        if (!talhoesCadastrados && !carregarMapaDoArquivo()) {
            System.out.println("Nenhum dado de talhões cadastrado. Use a opção 1 primeiro.");
            return;
        }

        int totalAlertas = 0;
        for (int i = 0; i < matrizUmidade.length; i++) {
            for (int j = 0; j < matrizUmidade[i].length; j++) {
                int umidade = matrizUmidade[i][j];
                String status = classificarTalhao(umidade);
                if (status.equals("Crítico") || status.equals("Atenção")) {
                    String codigo = obterCodigoTalhao(i, j);
                    System.out.println(codigo + " - " + status + " (" + umidade + "%)");
                    totalAlertas++;
                }
            }
        }
        System.out.println("Total de alertas: " + totalAlertas);
    }

    public static String montarTextoRelatorioGerencial() {
        int somaUmidade = 0;
        int totalTalhoes = 16;
        int adequados = 0;
        int criticos = 0;
        int necessitamIrrigacao = 0;

        int menorUmidade = Integer.MAX_VALUE;
        int maiorUmidade = Integer.MIN_VALUE;
        String talhaoMaisSeco = "";
        String talhaoMaisUmido = "";

        for (int i = 0; i < matrizUmidade.length; i++) {
            for (int j = 0; j < matrizUmidade[i].length; j++) {
                int umidade = matrizUmidade[i][j];
                String codigo = obterCodigoTalhao(i, j);
                String status = classificarTalhao(umidade);

                somaUmidade += umidade;

                if (status.equals("Adequado")) {
                    adequados++;
                } else {
                    necessitamIrrigacao++;
                    if (status.equals("Crítico")) {
                        criticos++;
                    }
                }

                if (umidade < menorUmidade) {
                    menorUmidade = umidade;
                    talhaoMaisSeco = codigo + "-" + umidade + "%";
                }
                if (umidade > maiorUmidade) {
                    maiorUmidade = umidade;
                    talhaoMaisUmido = codigo + "-" + umidade + "%";
                }
            }
        }

        int mediaGeral = somaUmidade / totalTalhoes;
        int percentualIrrigacao = (necessitamIrrigacao * 100) / totalTalhoes;
        String dataAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        StringBuilder sb = new StringBuilder();
        sb.append("===== RELATORIO DA FAZENDA =====\n");
        sb.append("Data: ").append(dataAtual).append("\n");
        sb.append("Media Geral: ").append(mediaGeral).append("%\n");
        sb.append("Talhoes Criticos: ").append(criticos).append("\n");
        sb.append("Talhoes Adequados: ").append(adequados).append("\n");
        sb.append("Necessidade de Irrigacao: ").append(percentualIrrigacao).append("%\n");
        sb.append("Talhao mais seco: ").append(talhaoMaisSeco).append("\n");
        sb.append("Talhao mais umido: ").append(talhaoMaisUmido).append("\n");

        return sb.toString();
    }

    public static void gerarRelatorioGerencial() {
        System.out.println();
        if (!talhoesCadastrados && !carregarMapaDoArquivo()) {
            System.out.println("Nenhum dado de talhões cadastrado. Use a opção 1 primeiro.");
            return;
        }
        System.out.print(montarTextoRelatorioGerencial());
    }

    public static void exportarRelatorio() {
        if (!talhoesCadastrados && !carregarMapaDoArquivo()) {
            System.out.println("Nenhum dado de talhões cadastrado. Use a opção 1 primeiro.");
            return;
        }

        String conteudo = montarTextoRelatorioGerencial();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_RELATORIO))) {
            writer.write(conteudo);
            System.out.println("Relatório exportado com sucesso para " + ARQUIVO_RELATORIO);
        } catch (IOException e) {
            System.out.println("Erro ao exportar relatório: " + e.getMessage());
        }
    }

    public static boolean carregarMapaDoArquivo() {
        File arquivo = new File(ARQUIVO_TALHOES);
        if (!arquivo.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            reader.readLine();
            String linha;
            int count = 0;

            while ((linha = reader.readLine()) != null && count < 16) {
                if (linha.trim().isEmpty()) {
                    continue;
                }
                String[] partes = linha.split(",");
                if (partes.length >= 2) {
                    int i = count / 4;
                    int j = count % 4;
                    matrizUmidade[i][j] = Integer.parseInt(partes[1].trim());
                    count++;
                }
            }

            if (count == 16) {
                talhoesCadastrados = true;
                return true;
            }
        } catch (IOException | NumberFormatException e) {
            return false;
        }
        return false;
    }
}
```[cite: 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]