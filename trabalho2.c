#include<stdio.h>
#include <stdlib.h>
#include<omp.h>
#include <time.h>
#include <assert.h>

#define TAM 2048
#define INTERACTIONS 10
#define NTHREADS 2

int getNeighbors(int** grid, int i, int j){
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

int main(int argc, char *argv[]){
    int **grid,**newgrid,aux, count = 0;
    int i,j,k;
    
    grid = calloc(TAM, sizeof(int *));
    for(i = 0; i < TAM+1; i++)
        grid[i] = calloc(TAM,sizeof(int));

    newgrid = calloc(TAM, sizeof(int *));
    for(i = 0; i < TAM+1; i++)
        newgrid[i] = calloc(TAM,sizeof(int));

    //GLIDER
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

    printf("Condição inicial: 10\n");

    double start, end;

    start = omp_get_wtime();

    for ( k = 0; k < INTERACTIONS; k++)
    {   
        #pragma omp parallel num_threads (NTHREADS)
        {
        #pragma omp parallel for private(i,j,aux)
        for ( i = 0; i < TAM; i++)
        {
            for (j = 0; j < TAM; j++)
            {
                aux = getNeighbors(grid,i,j);
                if((aux == 2||aux == 3) && grid[i][j] == 1) newgrid[i][j]=1;
                else if((aux == 6||aux == 3) && grid[i][j] == 0) newgrid[i][j]=1;
                else newgrid[i][j]=0;
            }
            
        }
        }
        #pragma omp parallel num_threads (NTHREADS)
        {
        #pragma omp parallel for private(i,j)\
            reduction(+:count)
        for ( i = 0; i < TAM; i++)
        {
            for (j = 0; j < TAM; j++)
            {
               grid[i][j] = newgrid[i][j];
               if(grid[i][j] == 1) count++;  
            }
            
        } 
        }

        printf("Geração %d: %d\n", k+1,count);
        count = 0;
    
    }
    end = omp_get_wtime();
    printf(" Demorou %f segundos.\n", end-start);

    return 0;
}