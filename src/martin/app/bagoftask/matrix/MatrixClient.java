package martin.app.bagoftask.matrix;


public class MatrixClient {
	
   //Sejam as matrizes Am,p e Bp,n (o número de colunas da primeira deve ser igual 
   //ao número de linhas da segunda). O produto AB é dado pela matriz Cm,n cujos 
   //elementos são calculados por:
   //  cij = ?k=1,p aik bkj. 


   public static void main(String[] args) {
      String lineFile   = System.getProperty("line");
      String matrixFile = System.getProperty("matrix");
      
      int[]   line   = Matrix.readLine(lineFile);
      int[][] matrix = Matrix.readMatrix(matrixFile);
      
      if ( line==null || matrix==null ) {
         System.out.println("\nIt was not possible to execute the matrix multiplication: ");
         if ( line==null ) { 
            System.out.println("> Line is empty.");
         }
         if ( matrix==null ) {
            System.out.println("> Matrix is empty.");
         }
         System.out.println("");
         System.exit(1); // abnormal exit > failled
      }
      
      int[] result   = Matrix.multiply(matrix, line);
      Matrix.showLine(result);
      //File  resultFile = Matrix.saveResultLine(result, lineFile);
   }
   
}
