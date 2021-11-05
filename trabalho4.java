import java.lang.*;
import java.lang.reflect.Constructor;


public class trabalho4 implements Runnable {
  int TAM,inicio,fim;
  int[][] grid;
  int[][] newgrid;

  public trabalho4(int[][] grid, int[][] newgrid,int n, int inicio, int fim){
    this.grid = grid;
    this.newgrid = newgrid;
    this.TAM = n;
    this.inicio = inicio;
    this.fim = fim;
  }

  public int getNeighbors(int[][] grid, int i , int j){
    int neighbors = 0, k,m, lin, col;

      for (k = 0; k < 3; k++)
      {
        for(m = 0; m < 3; m++){
            lin = i-1+k;
            col = j-1+m;
            if(lin < 0) lin = TAM-1;
            else if(lin >= TAM)lin = 0; 
            if(col < 0) col = TAM-1;
            else if(col >= TAM)col = 0;
            if(grid[lin][col] == 1 && (lin != i || col != j))neighbors = neighbors + 1;
        }
    }    
    return neighbors;

  }
  public void run(){
    int i,j,aux;
    for(i=inicio;i<=fim;i++){
      for (j = 0; j < TAM; j++)
      {
          aux = getNeighbors(grid,i,j);
          if((aux == 2||aux == 3) && grid[i][j] == 1) newgrid[i][j]=1;
          else if((aux == 6||aux == 3) && grid[i][j] == 0) newgrid[i][j]=1;
          else newgrid[i][j]=0;
      }
    }
  }

  public static void main(String args[]) {
    int TAM = 2048, iteracoes = 2000, aux = 0,NTHREADS = 2, i=0,j=0,div,count=0;
    int k;
    int[][] grid = new int[TAM][TAM];
    int[][] newgrid = new int[TAM][TAM];

    int lin = 1, col = 1;
    grid[lin  ][col+1] = 1;
    grid[lin+1][col+2] = 1;
    grid[lin+2][col  ] = 1;
    grid[lin+2][col+1] = 1;
    grid[lin+2][col+2] = 1;
 
    //R-pentomino
    lin =10; col = 30;
    grid[lin][col+1] = 1;
    grid[lin][col+2] = 1;
    grid[lin+1][col ] = 1;
    grid[lin+1][col+1] = 1;
    grid[lin+2][col+1] = 1;
    
    long startTime = System.currentTimeMillis();
    
    System.out.printf("Condição inicial: 10\n");

    for(i = 0; i < iteracoes; i++){
      trabalho4[] arraytrabalho = new trabalho4[NTHREADS];
      Thread[] arraythread = new Thread[NTHREADS];
      div = TAM/NTHREADS ;

      for(j = 0;j<NTHREADS;j++){
        arraytrabalho[j] = new trabalho4(grid,newgrid,TAM,aux,aux+div-1);
        arraythread[j] = new Thread(arraytrabalho[j]);
        arraythread[j].start();
        aux = aux+div;
      }
      aux = 0;

      for (j = 0; j < NTHREADS; j++){
        try {
          arraythread[j].join();
        } 
        catch (InterruptedException e) {
            e.printStackTrace();
          }
      }

      for ( k = 0; k < TAM; k++)
        {
            for (j = 0; j < TAM; j++)
            {
               grid[k][j] = newgrid[k][j];
               if(grid[k][j] == 1) count++;  
            }  
        }

      System.out.printf("Geração %d: %d\n", i ,count);
      count = 0;    
    
    }
    long endTime = System.currentTimeMillis();
    System.out.println("Levou " + (endTime - startTime) + " milissegundos");
  }
}