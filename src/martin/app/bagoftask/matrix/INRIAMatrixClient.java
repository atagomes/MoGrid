package martin.app.bagoftask.matrix;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class INRIAMatrixClient {

    //Sejam as matrizes Am,p e Bp,n (o n�mero de colunas da primeira deve ser igual 
    //ao n�mero de linhas da segunda). O produto AB � dado pela matriz Cm,n cujos 
    //elementos s�o calculados por:
    //  cij = ?k=1,p aik bkj. 
    private static final int LOOP_SIZE = 400000000;

    public static void main(String[] args) throws IOException {
        long t_ini = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");

        String homeDir = System.getProperty("user.home");
        BufferedWriter bw = null;
        BufferedWriter bw2 = null;
        bw = new BufferedWriter(new FileWriter(homeDir + "/Desktop/makespam_detailed.txt", true));
        bw2 = new BufferedWriter(new FileWriter(homeDir + "/Desktop/makespam_simple.txt", true));
        String ini_date = sdf.format(new Date());
        String ini_date2 = sdf2.format(new Date());
        bw.write(ini_date + "\t");
        bw2.write(ini_date2 + "\t");

        int[] result = {};

        String lineFile = System.getProperty("line");
        String matrixFile = System.getProperty("matrix");

        int[] line = Matrix.readLine(lineFile);
        int[][] matrix = Matrix.readMatrix(matrixFile);

        if (line == null || matrix == null) {
            System.out.println("\nIt was not possible to execute the matrix multiplication: ");
            if (line == null) {
                System.out.println("> Line is empty.");
            }
            if (matrix == null) {
                System.out.println("> Matrix is empty.");
            }
            System.out.println("");
            System.exit(1); // abnormal exit > failled

        }

        //O objetivo desse loop eh intensificar a carga de processamento no colaborador
        //para realizar medicoes em funcao da informacao de contexto do dispositivo
        for (int i = 0; i < LOOP_SIZE; i++) {
            result = Matrix.multiply(matrix, line);
        }
        Matrix.showLine(result);
        long t_end = System.currentTimeMillis();

        bw.write(String.valueOf(t_end - t_ini) + "\t");

        String end_date = sdf.format(new Date());
        String end_date2 = sdf2.format(new Date());
        bw.write(end_date + "\n");
        bw2.write(end_date2 + "\n");
        bw.flush();
        bw2.flush();

    //File  resultFile = Matrix.saveResultLine(result, lineFile);
    }
}
