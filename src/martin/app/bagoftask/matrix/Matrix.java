package martin.app.bagoftask.matrix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import martin.mogrid.common.util.MoGridString;

public class Matrix {

   private static String basedir = "";
   
   public static void setBaseDir(String dir) {
      Matrix.basedir = dir + File.separator;
   }

   public static String getBaseDir() {
      return Matrix.basedir;
   }

   //PARA SALVAR...
   private static String mountFilePath(String fileName) {
      return basedir + fileName;
   }
   private static String mountMatrixFilePath(String name) {
      return basedir + name + ".properties";
   }
   private static String mountLineFilePath(String name) {
      return basedir + name + ".properties";
   }
   private static String mountResultLineFilePath(String name) {
      return basedir + "Result" + name;
   }
   
   //MATRIX
   public static int[][] create(int rows, int columns, int rand) {
      int[][] matrix = new int[rows][columns];
      for (int r = 0; r < rows; r++) {
         for (int c = 0; c < columns; c++) {
            matrix[r][c] = (int) (Math.random() * rand);
         }
      }
      return matrix;
   }
   
   public static int[][] create(int rows, int columns) {
     return Matrix.create(rows, columns, 10);
   }

   public static File create(int rows, int columns, String name) {
      int[][] matrix = Matrix.create(rows, columns);
      return Matrix.save(matrix, name);
   }

   public static File create(int rows, int columns, int rand, String name) {
      int[][] matrix = Matrix.create(rows, columns, rand);
      return Matrix.save(matrix, name);
   }
   
   //Sejam as matrizes Am,p e Bp,n (o número de colunas da primeira deve ser igual 
   //ao número de linhas da segunda). O produto AB é dado pela matriz Cm,n cujos 
   //elementos são calculados por:
   //  cij = ?k=1,p aik bkj. 
   public static int[][] multiply(int[][] matA, int[][] matB) {
      int[][] matAB = null;
      
      if ( matA==null || matB==null ) {
         System.out.println("[ERRO] Uma das matrizes é nula.");
         System.exit(1); // abnormal exit > failled
      }
      
      if (matA[0].length == matB.length) {
         matAB = new int[matA.length][matB[0].length];
         for (int linha = 0; linha < matA.length; linha++) {
            for (int coluna = 0; coluna < matB[0].length; coluna++) {
               int acumula_somaprod = 0;
               for (int i = 0; i < matA[0].length; i++) {
                  //System.out.print("mat1[" + linha + "][" + i + "]*mat2[" + i + "][" + coluna + "]: ");
                  //System.out.println(matA[linha][i] + " * " + matB[i][coluna] + "= " + matA[linha][i] * matB[i][coluna]);
                  acumula_somaprod += matA[linha][i] * matB[i][coluna];
               }
               matAB[linha][coluna] = acumula_somaprod;
               //System.out.print("\n");
            }
         }
      } else {
         System.out.println("[ERRO] A quantidade de colunas da matriz A tem que ser igual a quantidade de linhas da matriz B.");
         System.exit(1); // abnormal exit > failled
      }
      return matAB;
   }
      
   public static File save(int[][] matrix, String name) {
      if ( matrix==null ) {
         System.out.println("Matriz nula: não foi possível salvar a matriz no arquivo " + name + ".");
         return null;
      }
      
      String filePath = mountMatrixFilePath(name);
      //System.out.println("matrix file name: "+name+" "+filePath);
      Properties properties = new Properties();          
      for (int r = 0; r < matrix.length; r++) {
         for (int c = 0; c < matrix[0].length; c++) {
            String mt = String.valueOf(matrix[r][c]);
            if (properties.get("Line" + r) == null)
               properties.put("Line" + r, mt);
            else
               properties.put("Line" + r, properties.get("Line" + r) + "," + mt);
         }
      }
      try {
         properties.store(new FileOutputStream(filePath), null);
         return new File(filePath);
      } catch (IOException e) {
         System.out.println("Não foi possível salvar a matriz no arquivo " + name + ".");
         return null;
      }
   }
   
   public static void show(int[][] matrix) { 
      for (int linha = 0; linha < matrix.length; linha++) {
         for (int coluna = 0; coluna < matrix[0].length; coluna++)
            System.out.print(matrix[linha][coluna] + " ");
         System.out.print("\n");
      }
   }
   
   public static int[][] readMatrix(String fileName) {
      Properties properties = new Properties();
      String matrixPath = mountFilePath(fileName); 
      int[][] matriz = null;
      try {
          properties.load(new FileInputStream(matrixPath));
      } catch (IOException e) {
         System.out.println("Arquivo " + fileName + " não foi encontrado.");
         System.exit(1); // abnormal exit > failled
      }      
      
      int columns = 0;
      int linhas  = properties.size();
      if ( linhas > 0 ) {
         String property = properties.getProperty((String)properties.propertyNames().nextElement());
         //columns = property.split(",").length;
         columns = MoGridString.split(property, ",").length;
         matriz = new int[linhas][columns];
      }
      for ( int l=0; l<linhas; l++ ) {
         String prop = "Line" + l;
         String linha = properties.getProperty(prop);
         if (linha != null) {
            //String[] aux = linha.split(",");
            String[] aux = MoGridString.split(linha, ",");
            for (int c = 0; c < columns; c++) {
               matriz[l][c] = Integer.parseInt(aux[c]);
               //System.out.println("line["+r+"]["+c+"] = "+ Integer.parseInt(aux[c]));
            }
         }
      }
      return matriz;
   }
   
   
   //Matrix LINE   
   public static void showLine(int[] line) { 
      for (int coluna = 0; coluna < line.length; coluna++)
         System.out.print(line[coluna] + " ");
      System.out.print("\n");
   }

   private static File save(int[] line, String linePath) {
      Properties properties = new Properties(); 
      int columns = line.length;
      for (int c = 0; c < columns ; c++) {
         String mt = String.valueOf(line[c]);
         if (properties.get("Line") == null)
            properties.put("Line", mt);
         else
            properties.put("Line", properties.get("Line") + "," + mt);
      }
      try {
         properties.store(new FileOutputStream(linePath), null);
         return new File(linePath);
      } catch (IOException e) {
         System.out.println("Não foi possível salvar a linha no arquivo " + linePath + ".");
         return null;
      }
   }
   
   public static File saveLine(int[] line, String name) {
      String linePath = mountLineFilePath(name);
      return save(line, linePath);
   }

   public static File saveResultLine(int[] line, String name) {
      String linePath = mountResultLineFilePath(name);
      return save(line, linePath);
   }

   public static int[] readLine(String fileName) {
      Properties properties = new Properties();       
      int[] line = null;
      try {
          properties.load(new FileInputStream(fileName));
      } catch (IOException e) {
         System.out.println("Arquivo " + fileName + " não foi encontrado.");
         System.exit(1); // abnormal exit > failled
      }
      String linha = properties.getProperty("Line");
      if(linha != null) {
         //String[] aux = linha.split(",");
         String[] aux = MoGridString.split(linha, ",");
         line = new int[aux.length];
         for(int j=0;j<aux.length;j++) {
            line[j] = Integer.parseInt(aux[j]);
         }
         return line;
      }
      return null;      
   }
   
   public static int[] readLineFromMatrix(String matrixFile, int numLine) {
      int[] line = null;
      Properties properties = new Properties();
      String matrixPath = mountMatrixFilePath(matrixFile);
      try {
         properties.load(new FileInputStream(matrixPath));
      } catch (IOException e) {
         System.out.println("Arquivo " + matrixFile + " não foi encontrado.");
         System.exit(1); // abnormal exit > failled
      }
      String prop = "Line"+numLine;
      String linha = properties.getProperty(prop);
      if (linha != null) {
         //String[] aux = linha.split(",");
         String[] aux = MoGridString.split(linha, ",");
         int columns = aux.length;
         line = new int[columns];
         for (int c = 0; c < columns; c++) {
            line[c] = Integer.parseInt(aux[c]);
            //System.out.println("line["+r+"]["+c+"] = "+ Integer.parseInt(aux[c]));
         }
      }
      return line;
   }


   //Tenho duas matrizes Am,p e Bp,n, onde A * B = Cm,n
   //B * lineA = ok {r(B)=c(lineA)}  -  A * lineB = erro {r(A)!=c(lineB)}  
   public static int[] multiply(int[][] mat, int[] line) {
      int[] result = null;
      //r(mat) = c(line)
      if (mat.length == line.length) {
         result = new int[mat[0].length];
            for (int c=0; c<mat[0].length; c++ ) {
               int soma = 0;
               for ( int j=0; j<line.length; j++ ) {
                  //System.out.println("line["+j+"]*mat["+j+"]["+c+"]: "+line[j]+"*"+mat[j][c]+"= "+line[j]*mat[j][c]);
                  soma += line[j]*mat[j][c];
               }
               result[c] = soma;
            }
      } else {
         //System.out.println(mat[0].length +" == "+line.length+" [ERRO] A quantidade de linhas da matriz tem que ser igual a quantidade de colunas da linha.");
         System.exit(1); // abnormal exit > failled
      }
      return result;
   }

   public static File[] extractLines(File fileName) {
      String filePath = fileName.getAbsolutePath();
      return extract(filePath);
   }

   public static File[] extractLines(String name) {
      String filePath = mountMatrixFilePath(name);      
      return extract(filePath); 
   }
   
   private static File[] extract(String filePath) {
      File[] lineFiles = null;
      int[] line = null;
      Properties properties = new Properties();
      try {
         properties.load(new FileInputStream(filePath));         
      } catch (IOException e) {
         System.out.println("Arquivo " + filePath + " não foi encontrado.");
         System.exit(1); // abnormal exit > failled
      }
      Enumeration lines = properties.propertyNames();
      lineFiles = new File[properties.size()];
      int f = 0;
      while ( lines.hasMoreElements() ) {
         String prop = (String)lines.nextElement();
         String linha = properties.getProperty(prop);
         if (linha != null) {
            //String[] aux = linha.split(",");
            String[] aux = MoGridString.split(linha, ",");
            int columns = aux.length;
            line = new int[columns];
            for (int c = 0; c < columns; c++) {
               line[c] = Integer.parseInt(aux[c]);
               //System.out.println("line["+r+"]["+c+"] = "+ Integer.parseInt(aux[c]));
            }
            //System.out.print(prop + ": ");
            //showLine(line);
            lineFiles[f++] = saveLine(line, prop);
         }
      }
      return lineFiles;
   }
   

   public static void main(String[] args) {
      //File mat = Matrix.create(3, 2, "A");
      int[][] vet = Matrix.readMatrix("A");
      Matrix.show(vet);
      int[] line = Matrix.readLineFromMatrix("A", 0);
      Matrix.showLine(line);
      
      //int[][] mat2 = Matrix.create(2, 3, "B");
      //System.out.println("\n");
      //Matrix.extractLines("A");
      //int[][] line1 = Matrix.getLine("A");
      //Matrix.show(line1);
      //int[][] line2 = Matrix.getLine("A");
      //Matrix.show(line2);
      //int[][] mat3 = Matrix.multiply(mat1, mat2);
      //Matrix.show(mat3);
      
   }
}
