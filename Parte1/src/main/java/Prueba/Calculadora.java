package Prueba;

import java.util.Iterator;

import javax.ws.rs.MatrixParam;

import java.util.ArrayList;


public class Calculadora {
    private static Calculadora single_instance = null;

    private Calculadora(){

    }

    public static Calculadora getInstance(){
        if(single_instance == null){
            single_instance = new Calculadora();
        }
        return single_instance;
    }


    public void calculaProbabilidades(ArrayList<Nodo> lista, int lecturasT) {
        Iterator<Nodo> it = lista.iterator();
        Nodo act;
        while (it.hasNext()) {
            act = it.next();
            act.setProbabilidad(lecturasT);
        }
    }

    
    public double log2(double numero) { // Logaritmo en base 2 (hay que crear la funcion porque java no la trae)
        double result = (double) (Math.log(numero) / Math.log(2));
        return result;
    }

    public double calculaEntropia(ArrayList<Nodo> lista) {
        double entropia = 0;
        double probabilidad;
        for (int i = 0; i < lista.size(); i++) {
            probabilidad = lista.get(i).getProbabilidad();
            entropia += probabilidad * log2(1 / probabilidad);
        }
        return entropia;
    }

    public double [][] multiplicaMatrizCuadrada(double [][] matrizA , double [][] matrizB){

        double [][] result = new double[matrizA.length][matrizB.length];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result.length; j++) {
                result[i][j] = 0;
                for (int k = 0; k < result.length; k++) {
                    result[i][j]+= matrizA[i][k] * matrizB[k][j];
                }
            }
        }
        return result;
    }


    public double [][] calculaPotenciaMatriz(double [][] matriz, int p){
        double [][] resultado = matriz;
        for (int i = 1; i < p; i++) {
            resultado = multiplicaMatrizCuadrada(resultado, matriz);
        }
        return resultado;
    }
}