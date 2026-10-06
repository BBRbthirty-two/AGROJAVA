import java.util.Scanner;

public class AGROJAVA {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int[] dias = { 1, 2, 3, 4, 5, 6, 7 };
        float volumeChuva = 0;
        for (int i = 0; i < dias.length; i++) {
            System.out.print("Digite o volume de chuva do dia " + dias[i] + ": ");
            volumeChuva += scanner.nextFloat();
        }
        float mediaChuva = volumeChuva / dias.length;
        System.out.println("A média de chuva da semana foi: " + mediaChuva);
        int contador = 0;
        int[][] matriz = new int[4][4];
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.print("Informe a porcentagem de umidade do talhão  [" + i + "][" + j + "]: ");
                int porcentagem = scanner.nextInt();
                if (porcentagem < 30) {
                        System.out.println("O talhão " + "[" + i + "][" + j + "] necessita de irrigação." ); contador++; 
                } 
            }
        }
                   System.out.println("O número de talhões que necessitam de irrigação é: " + contador);
    }
}