package modelo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class Calculadora {

	// Huffman
	public static ArrayList<NodoHuffman> armaArrayParaHuffman(ArrayList<Nodo> palabras) {
		NodoHuffman nuevo;
		ArrayList<NodoHuffman> simbolos = new ArrayList<NodoHuffman>();
		int i = 0;
		while (i < palabras.size()) {
			nuevo = new NodoHuffman();
			nuevo.setProbabilidad(palabras.get(i).getProbabilidad());
			nuevo.agregarIndice(i);
			simbolos.add(nuevo);
			i++;
		}
		burbujaH(simbolos);
		return simbolos;
	}

	public static void huffman(ArrayList<NodoHuffman> simbolosHuffman, ArrayList<Nodo> palabras) {
		if (simbolosHuffman.size() == 2) {
			asigna0y1(simbolosHuffman.get(0).getIndices(), palabras, 0);
			asigna0y1(simbolosHuffman.get(1).getIndices(), palabras, 1);
		} else {
			// suma los dos ultimos
			NodoHuffman nuevo = new NodoHuffman();
			int ultimo = simbolosHuffman.size() - 1;
			int anteUltimo = simbolosHuffman.size() - 2;
			NodoHuffman NHUltimo = simbolosHuffman.get(ultimo);
			NodoHuffman NHAnteUltimo = simbolosHuffman.get(anteUltimo);
			nuevo.setProbabilidad(NHUltimo.getProbabilidad() + NHAnteUltimo.getProbabilidad());
			agregaTodosLosIndices(nuevo, NHUltimo.getIndices());
			agregaTodosLosIndices(nuevo, NHAnteUltimo.getIndices());
			simbolosHuffman.set(anteUltimo, nuevo);
			simbolosHuffman.remove(ultimo);
			// ordena de nuevo
			burbujaH(simbolosHuffman);
			// llama recursividad
			huffman(simbolosHuffman, palabras);
			// aisgna 0 y 1 a los dos que se sumaron
			asigna0y1(NHAnteUltimo.getIndices(), palabras, 0);
			asigna0y1(NHUltimo.getIndices(), palabras, 1);
		}
	}

	private static void burbujaH(ArrayList<NodoHuffman> A) {
		int i, j;
		NodoHuffman aux;
		for (i = 0; i < A.size() - 1; i++) {
			for (j = 0; j < A.size() - i - 1; j++) {
				if (A.get(j + 1).getProbabilidad() > A.get(j).getProbabilidad()) {
					aux = A.get(j + 1);
					A.set(j + 1, A.get(j));
					A.set(j, aux);
				}
			}
		}
	}

	private static void asigna0y1(ArrayList<Integer> indices, ArrayList<Nodo> palabras, Integer digito) {
		Iterator<Integer> it = indices.iterator();
		while (it.hasNext()) {
			palabras.get(it.next()).agregarDigitoAHuffman(digito);
		}
	}

	private static void agregaTodosLosIndices(NodoHuffman nuevo, ArrayList<Integer> indices) {
		Iterator<Integer> it = indices.iterator();
		while (it.hasNext()) {
			nuevo.agregarIndice(it.next());
		}
	}

	public static String resultadosHuffman(ArrayList<Nodo> palabras) {
		String respuesta = "";
		Iterator<Nodo> it = palabras.iterator();
		Nodo actual;
		while (it.hasNext()) {
			actual = it.next();
			respuesta += " Palabra codigo =  " + actual.getPalabra() + "   |  Codigo Huffman  "
					+ actual.getPalabraHuffman() + "\n";
		}
		return respuesta;
	}

	// Shanon - Fano

	private static void burbuja(ArrayList<NodoShanonFano> A) {
		int i, j;
		NodoShanonFano aux;
		for (i = 0; i < A.size() - 1; i++) {
			for (j = 0; j < A.size() - i - 1; j++) {
				if (A.get(j + 1).getProbabilidad() > A.get(j).getProbabilidad()) {
					aux = A.get(j + 1);
					A.set(j + 1, A.get(j));
					A.set(j, aux);
				}
			}
		}
	}

	public static ArrayList<NodoShanonFano> armaArrayParaShanonFano(ArrayList<Nodo> palabras) {
		NodoShanonFano nuevo;
		ArrayList<NodoShanonFano> simbolos = new ArrayList<NodoShanonFano>();
		int i = 0;
		while (i < palabras.size()) {
			nuevo = new NodoShanonFano();
			nuevo.setProbabilidad(palabras.get(i).getProbabilidad());
			nuevo.setIndiceOriginal(i);
			simbolos.add(nuevo);
			i++;
		}
		burbuja(simbolos);
		return simbolos;
	}

	private static double calculaDiferenciaShanon(ArrayList<NodoShanonFano> palabrasShanonFano, int k) {
		double probabilidadAcumulada = 0;
		for (int i = 0; i < k; i++) {
			probabilidadAcumulada += palabrasShanonFano.get(i).getProbabilidad();
		}
		double restoProbabilidad = 0;
		for (int i = k; i < palabrasShanonFano.size(); i++) {
			restoProbabilidad += palabrasShanonFano.get(i).getProbabilidad();
		}
		return Math.abs(probabilidadAcumulada - restoProbabilidad);
	}

	public static void shanonFano(ArrayList<NodoShanonFano> palabrasShanonFano, ArrayList<Nodo> palabrasOriginal) {
		if (palabrasShanonFano.size() > 1) {
			if (palabrasShanonFano.size() == 2) {
				palabrasOriginal.get(palabrasShanonFano.get(0).getIndiceOriginal()).agregarDigitoShanonFano(0);
				palabrasOriginal.get(palabrasShanonFano.get(1).getIndiceOriginal()).agregarDigitoShanonFano(1);
			} else {
				// calcula el valor de k
				double minimaDiferencia = 1;
				int k = 0;
				for (int i = 1; i < palabrasShanonFano.size(); i++) {
					double diferencia = calculaDiferenciaShanon(palabrasShanonFano, i);
					if (diferencia <= minimaDiferencia) {
						minimaDiferencia = diferencia;
						k = i;
					}
				}
				ArrayList<NodoShanonFano> primerSubconjunto = new ArrayList<NodoShanonFano>();
				ArrayList<NodoShanonFano> segundoSubconjunto = new ArrayList<NodoShanonFano>();
				// una vez calculado el valor de k, se asigna 0 a los primeros k y 1 a los demas
				for (int i = 0; i < k; i++) {
					primerSubconjunto.add(palabrasShanonFano.get(i));
					palabrasOriginal.get(primerSubconjunto.get(i).getIndiceOriginal()).agregarDigitoShanonFano(0);
				}
				for (int i = k; i < palabrasShanonFano.size(); i++) {
					segundoSubconjunto.add(palabrasShanonFano.get(i));
					palabrasOriginal.get(palabrasShanonFano.get(i).getIndiceOriginal()).agregarDigitoShanonFano(1);
				}
				// llamado recursivo con cada subconjunto
				shanonFano(primerSubconjunto, palabrasOriginal);
				shanonFano(segundoSubconjunto, palabrasOriginal);
			}
		}
	}

	public static String resultadosShanonFano(ArrayList<Nodo> palabras) {
		String respuesta = "";
		Iterator<Nodo> it = palabras.iterator();
		Nodo actual;
		while (it.hasNext()) {
			actual = it.next();
			respuesta += " Palabra codigo =  " + actual.getPalabra() + "   |  Codigo Shanon-Fano  "
					+ actual.getPalabraShanonFano() + "\n";
		}
		return respuesta;
	}

	public static String RLC(String path) {
		String respuestaRLC = "";
		final StringBuilder builder = new StringBuilder();
		try {
			if (path.contains(".txt")) {
				String source = Files.readString(Paths.get(path));
				ArrayList<Character> uniqueChars = new ArrayList<Character>();
				int repeticionesMax = 0;
				for (int i = 0; i < source.length(); i++) {
					int repeticiones = 1;
					while (i + 1 < source.length() && source.charAt(i) == source.charAt(i + 1)) {
						repeticiones++;
						i++;
					}
					if (repeticiones > repeticionesMax) {
						repeticionesMax = repeticiones;
					}
					if (!uniqueChars.contains(source.charAt(i))) {
						uniqueChars.add(source.charAt(i));
					}
				}
				int cadenasDistintas = uniqueChars.size();
				int bytesRepeticiones = (int) Math.ceil(Math.log(repeticionesMax) / (Math.log(2) * 8));
				int bytesCodigo = (int) Math.ceil(Math.log(cadenasDistintas) / (Math.log(2) * 8));
				for (int i = 0; i < source.length(); i++) {
					int repeticiones = 1;

					while (i + 1 < source.length() && source.charAt(i) == source.charAt(i + 1)) {
						repeticiones++;
						i++;
					}
					String simboloBinario = Integer.toBinaryString(source.charAt(i));
					String padding1 = String.format("%" + bytesCodigo * 8 + "s", simboloBinario.replace(' ', '0'))
							.replace(' ', '0');
					builder.append(padding1);
					String padding = String
							.format("%" + bytesRepeticiones * 8 + "s", Integer.toBinaryString(repeticiones))
							.replace(' ', '0');
					builder.append(padding);
					builder.append(Integer.toBinaryString(repeticiones));
				}
				System.out.println("PATH: " + path);
				System.out.println("BYTES NEEDED REPETICIONES: " + bytesRepeticiones);
				System.out.println("BYTES NEEDED CODIGO: " + bytesCodigo);
			} else {
				if (path.contains(".raw")) {
					System.out.println("Entra al raw");
					FileReader f = new FileReader(path);
					BufferedReader b = new BufferedReader(f);
					String pixels1 = b.readLine();
					String pixels2 = b.readLine();
					String cadenaAnterior = b.readLine();
					String cadenaActual = b.readLine();
					int repeticiones = 0;
					// array of unique elements
					ArrayList<String> uniqueChars = new ArrayList<String>();
					int repeticionesMax = 0;
					// primera pasada para saber la cantidad de bytes necesarios para representar
					// cada simbolo y sus repeticiones
					while (cadenaActual != null) {
						if (!uniqueChars.contains(cadenaActual)) {
							uniqueChars.add(cadenaActual);
						}
						while (cadenaActual != null && cadenaActual.equals(cadenaAnterior)) {
							repeticiones++;
							cadenaAnterior = cadenaActual;
							cadenaActual = b.readLine();
						}
						if (repeticiones > repeticionesMax)
							repeticionesMax = repeticiones;
						cadenaAnterior = cadenaActual;
						repeticiones = 0;
					}
					b.close();
					// segunda pasada sabiendo cuantos bytes necesito de repeticiones y de codigo

					f = new FileReader(path);
					b = new BufferedReader(f);
					int iteraciones = 0;
					pixels1 = b.readLine();
					pixels2 = b.readLine();
					cadenaAnterior = b.readLine();
					cadenaActual = b.readLine();
					int aux = 0;
					int cadenasDistintas = uniqueChars.size();
					int bytesRepeticiones = (int) Math.ceil(Math.log(repeticionesMax) / (Math.log(2) * 8));
					int bytesCodigo = (int) Math.ceil(Math.log(cadenasDistintas) / (Math.log(2) * 8));
					while (cadenaActual != null) {
						iteraciones++;
						while (cadenaActual != null && cadenaActual.equals(cadenaAnterior)) {
							repeticiones++;
							cadenaAnterior = cadenaActual;
							cadenaActual = b.readLine();
							iteraciones++;
						}
						String simboloBinario = Integer.toBinaryString(Integer.parseInt(cadenaAnterior));
						String padding1 = String.format("%" + bytesCodigo * 8 + "s", simboloBinario.replace(' ', '0'))
								.replace(' ', '0');
						builder.append(padding1);
						String padding = String
								.format("%" + bytesRepeticiones * 8 + "s", Integer.toBinaryString(repeticiones))
								.replace(' ', '0');
						builder.append(padding);
						cadenaAnterior = cadenaActual;
						repeticiones = 0;

					}
					System.out.println("PATH: " + path);
					System.out.println("BYTES NEEDED REPETICIONES: " + bytesRepeticiones);
					System.out.println("BYTES NEEDED CODIGO: " + bytesCodigo);
				}
			}

		} catch (IOException e) {
			System.out.println("Error al abrir el archivo.");
		}

		respuestaRLC = builder.toString();
		return respuestaRLC;
	}

	public static double calculaEntropia(ArrayList<Nodo> palabras) {
		double entropia = 0;
		Iterator<Nodo> it = palabras.iterator();
		Nodo actual;
		while (it.hasNext()) {
			actual = it.next();
			entropia += (double) actual.getProbabilidad() * actual.getCantidadInformacion();
		}
		return entropia;
	}

	public static double longitudMedia(ArrayList<Nodo> palabras, String metodo) {
		double longitud = 0;
		int i = 0;
		if (metodo.equalsIgnoreCase("huffman"))
			while (i < palabras.size()) {
				longitud += palabras.get(i).getProbabilidad() * palabras.get(i).getPalabraHuffman().length();
				i++;
			}
		else if (metodo.equalsIgnoreCase("shanon-fano")) {
			while (i < palabras.size()) {
				longitud += palabras.get(i).getProbabilidad() * palabras.get(i).getPalabraShanonFano().length();
				i++;
			}
		}
		return longitud;
	}

	public static double rendimiento(ArrayList<Nodo> chars, String metodo) {
		System.out.println("Longitud media de " + metodo + ": " + longitudMedia(chars, metodo));
		return calculaEntropia(chars) / longitudMedia(chars, metodo);
	}

	public static double redundancia(ArrayList<Nodo> chars, String metodo) {
		return 1 - rendimiento(chars, metodo);
	}

}