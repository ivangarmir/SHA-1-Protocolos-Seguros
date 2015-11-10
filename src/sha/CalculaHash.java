package sha;

import java.nio.ByteBuffer;

/**
 *
 * @author ivan
 */
public class CalculaHash {

    int temp;
    int A, B, C, D, E;
    int[] H = {0x67452301, 0xEFCDAB89, 0x98BADCFE, 0x10325476, 0xC3D2E1F0};
    int F;
    static final String completo = "00000000";

    String calcular(byte[] bytesEntrada) {
        byte[] arrayDatosRelleno = rellenar(bytesEntrada);
        int[] K = {0x5A827999, 0x6ED9EBA1, 0x8F1BBCDC, 0xCA62C1D6};
        int numRep = arrayDatosRelleno.length / 64;
        byte[] datos = new byte[64];

        if (arrayDatosRelleno.length % 64 != 0) {
            System.out.println("Longitud de datos invalida");
            System.exit(-1);
        }

        for (int rep = 0; rep < numRep; rep++) {
            System.arraycopy(arrayDatosRelleno, 64 * rep, datos, 0, 64);
            procesaBloque(datos, H, K);
        }

        return arrIntHexToInt(H);
    }

    private byte[] rellenar(byte[] data) {
        int tamOriginal = data.length;
        int tamCola = tamOriginal % 64;
        int tamDesplaza;
        if ((64 - tamCola >= 9)) {
            tamDesplaza = 64 - tamCola;
        } else {
            tamDesplaza = 128 - tamCola;
        }

        byte[] procesado = new byte[tamDesplaza];
        procesado[0] = (byte) 0x80;
        long tamEnBits = (long) tamOriginal * 8;

        for (int cnt = 0; cnt < 8; cnt++) {
            procesado[procesado.length - 1 - cnt] = (byte) ((tamEnBits >> (8 * cnt)) & 0x00000000000000FF);
        }

        byte[] salida = new byte[tamOriginal + tamDesplaza];

        System.arraycopy(data, 0, salida, 0, tamOriginal);
        System.arraycopy(procesado, 0, salida, tamOriginal, procesado.length);

        return salida;

    }

    private void procesaBloque(byte[] entrada, int H[], int K[]) {

        int[] W = new int[80];
        for (int salida = 0; salida < 16; salida++) {
            int temp;
            for (int inner = 0; inner < 4; inner++) {
                temp = (entrada[salida * 4 + inner] & 0x000000FF) << (24 - inner * 8);
                W[salida] = W[salida] | temp;
            }
        }

        for (int j = 16; j < 80; j++) {
            W[j] = rotaIzq(W[j - 3] ^ W[j - 8] ^ W[j - 14] ^ W[j - 16], 1);
        }

        A = H[0];
        B = H[1];
        C = H[2];
        D = H[3];
        E = H[4];

        for (int j = 0; j <= 19; j++) {
            F = (B & C) | ((~B) & D);
            //	K = 0x5A827999;
            temp = rotaIzq(A, 5) + F + E + K[0] + W[j];
            // System.out.println(Integer.toHexString(K[0]));
            E = D;
            D = C;
            C = rotaIzq(B, 30);
            B = A;
            A = temp;
        }

        for (int j = 20; j <= 39; j++) {
            F = B ^ C ^ D;
            //   K = 0x6ED9EBA1;
            temp = rotaIzq(A, 5) + F + E + K[1] + W[j];
            //System.out.println(Integer.toHexString(K[1]));
            E = D;
            D = C;
            C = rotaIzq(B, 30);
            B = A;
            A = temp;
        }

        for (int j = 40; j <= 59; j++) {
            F = (B & C) | (B & D) | (C & D);
            //   K = 0x8F1BBCDC;
            temp = rotaIzq(A, 5) + F + E + K[2] + W[j];
            E = D;
            D = C;
            C = rotaIzq(B, 30);
            B = A;
            A = temp;
        }

        for (int j = 60; j <= 79; j++) {
            F = B ^ C ^ D;
            //   K = 0xCA62C1D6;
            temp = rotaIzq(A, 5) + F + E + K[3] + W[j];
            E = D;
            D = C;
            C = rotaIzq(B, 30);
            B = A;
            A = temp;
        }

        H[0] += A;
        H[1] += B;
        H[2] += C;
        H[3] += D;
        H[4] += E;

    }

    final int rotaIzq(int valor, int bits) {
        int q = (valor << bits) | (valor >>> (32 - bits));
        return q;
    }

    private String arrIntHexToInt(int[] entrada) {
        String salida = "";
        String stringTemp;
        int tempInt;
        for (int cnt = 0; cnt < entrada.length; cnt++) {
            tempInt = entrada[cnt];
            stringTemp = Integer.toHexString(tempInt);
            switch (stringTemp.length()) {
                case 1:
                    stringTemp = "0000000" + stringTemp;
                    break;
                case 2:
                    stringTemp = "000000" + stringTemp;
                    break;
                case 3:
                    stringTemp = "00000" + stringTemp;
                    break;
                case 4:
                    stringTemp = "0000" + stringTemp;
                    break;
                case 5:
                    stringTemp = "000" + stringTemp;
                    break;
                case 6:
                    stringTemp = "00" + stringTemp;
                    break;
                case 7:
                    stringTemp = "0" + stringTemp;
                    break;
                default:
                    break;
            }
            salida = salida + stringTemp;
        }
        return salida;
    }

    static final String toHexString(final ByteBuffer bb) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bb.limit(); i += 4) {
            if (i % 4 == 0) {
                sb.append('\n');
            }
            sb.append(toHexString(bb.getInt(i))).append(' ');
        }
        sb.append('\n');
        return sb.toString();
    }

    static final String toHexString(int x) {
        return strRellenado(Integer.toHexString(x));
    }

    static final String strRellenado(String s) {
        if (s.length() > 8) {
            return s.substring(s.length() - 8);
        }
        return completo.substring(s.length()) + s;
    }

}
