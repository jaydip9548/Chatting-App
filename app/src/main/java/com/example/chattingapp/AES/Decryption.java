package com.example.chattingapp.AES;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Decryption {
    static List<String> encryptedText;
    static String key;
   public  String decrypted_Text = "";


    public Decryption(List<String> text, String key) {
        encryptedText = new ArrayList<>(text);
        this.key = key;

        //        find Keys
        Map<Integer, List<String>> keys = findAllkeys();

        for (int i = 0; i < encryptedText.size(); i += 16) {
            List<String> list = new ArrayList<>(encryptedText.subList(i, i + 16));

            doDecryption(list, keys);
        }

    }

    private void doDecryption(List<String> encrypt, Map<Integer, List<String>> keys) {


//        Add RoundKeys : encrypt ^ map.get(10)
        List<String> data = RoundKeyOperation(encrypt, keys.get(10));

        for (int i = 9; i >= 0; i--) {
            String value[][] = new String[4][4];
            if (i == 0) {
                data = InverseshiftRows(data);
                data = InverseSbox(data);
                data = RoundKeyOperation(data, keys.get(i));


            } else {
                data = InverseshiftRows(data);
                data = InverseSbox(data);
                data = RoundKeyOperation(data, keys.get(i));
//                System.out.println("Round " + i + " " + data);
                int count = -1;
                for (int p = 0; p < 4; p++) {
                    for (int j = 0; j < 4; j++) {
                        count++;
                        value[j][p] = data.get(count);
                    }
                }

//                MixCol m1 = new MixCol();
//                value = m1.InvMixColumns(value);
                value = InvMixColumns(value);

                data.clear();
                for (int p = 0; p < 4; p++) {
                    for (int j = 0; j < 4; j++) {
                        data.add(value[j][p]);
                    }
                }
            }
        }
        for (int i = 0; i < data.size(); i++) {
            decrypted_Text += data.get(i);
        }
    }

    private Map<Integer, List<String>> findAllkeys() {
        KeyGenerator k = new KeyGenerator(key);
        Map<Integer, List<String>> keys = k.mainFunction();
        return keys;
    }

    private static List<String> RoundKeyOperation(List<String> encrypt, List<String> strings) {

        List<String> repeatedafR = new ArrayList<>();
        String[][] data = new String[4][4];
        for (int p = 0; p < encrypt.size(); p++) {
            if (strings.get(p).length() == 3) {
                strings.set(p, strings.get(p).substring(1, 3));
            } else {
            }
            int result = Integer.parseInt(encrypt.get(p), 16) ^
                    Integer.parseInt(strings.get(p), 16);
            String s = Integer.toHexString(result).toUpperCase();
            if (s.length() < 2) {
                s = "0" + s;
            }
            repeatedafR.add(s);

        }

        return repeatedafR;
    }

    private static List<String> InverseSbox(List<String> afR) {

        for (int i = 0; i < afR.size(); i++) {
            String a = afR.get(i);
            int r = 0;
            int c = 0;
            r = Integer.parseInt(String.valueOf(a.charAt(0)), 16);
            c = Integer.parseInt(String.valueOf(a.charAt(1)), 16);

            afR.set(i, Table.INVERSE_S_BOX[r][c].toUpperCase());

        }
        return afR;
    }

    private static List<String> InverseshiftRows(List<String> afR) {
        String[][] ans = new String[4][4];
        int count = -1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                count++;
                ans[j][i] = afR.get(count);
            }
        }
//     Shift 1
        int shift = 0;
        String[] key = new String[4];

        for (int i = 1; i < 4; i++) {
            shift++;
            for (int j = 3; j >= 0; j--) {
                int a = j + shift;
                a = a % 4;
                key[a] = ans[i][j];
            }
            for (int j = 0; j < 4; j++) {
                ans[i][j] = key[j];
            }
        }

        afR.clear();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                afR.add(ans[j][i]);
            }
        }

        return afR;
    }

    public String[][] InvMixColumns(String[][] baseHexBlock) {
        String[][] newHexBlock = new String[4][4];
        int[][] InverseRijndaelGaloisField = {
                {14, 11, 13, 9},
                {9, 14, 11, 13},
                {13, 9, 14, 11},
                {11, 13, 9, 14}
        };

        for (int i = 0; i < newHexBlock.length; i++) {
            for (int j = 0; j < newHexBlock[0].length; j++) {
                newHexBlock[i][j] = InvMixColumnMultiplication(baseHexBlock, InverseRijndaelGaloisField, i, j);
            }
        }

        return newHexBlock;
    }

    public static long byTwoMultiply(long num) {
        if (num < 128)
            return 2 * num;
        else {
            long res = (2 * num) ^ 27;
            String resString = Long.toHexString(res).toUpperCase();
            while (resString.length() > 2) {
                StringBuilder hexSb = new StringBuilder(resString);
                hexSb.deleteCharAt(0);
                resString = hexSb.toString();
            }
            return Long.parseLong(resString, 16);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public static String InvMixColumnMultiplication(String[][] hexBlock, int[][] InverseRijndaelBlock, int row, int col) {
        String hexResult = "";
        long[] values = new long[4];

        for (int i = 0; i < values.length; i++) {
            switch (InverseRijndaelBlock[row][i]) {
                case 9:
                    values[i] = byTwoMultiply(Long.parseLong(hexBlock[i][col], 16));
                    values[i] = byTwoMultiply(values[i]);
                    values[i] = byTwoMultiply(values[i]);
                    values[i] = values[i] ^ Long.parseLong(hexBlock[i][col], 16);
                    break;
                //...............................................
                case 11:
                    values[i] = byTwoMultiply(Long.parseLong(hexBlock[i][col], 16));
                    values[i] = byTwoMultiply(values[i]);
                    values[i] = values[i] ^ Long.parseLong(hexBlock[i][col], 16);
                    values[i] = byTwoMultiply(values[i]);
                    values[i] = values[i] ^ Long.parseLong(hexBlock[i][col], 16);
                    break;
                //...............................................
                case 13:
                    values[i] = byTwoMultiply(Long.parseLong(hexBlock[i][col], 16));
                    values[i] = values[i] ^ Long.parseLong(hexBlock[i][col], 16);
                    values[i] = byTwoMultiply(values[i]);
                    values[i] = byTwoMultiply(values[i]);
                    values[i] = values[i] ^ Long.parseLong(hexBlock[i][col], 16);
                    break;
                //...............................................
                case 14:
                    values[i] = byTwoMultiply(Long.parseLong(hexBlock[i][col], 16));
                    values[i] = values[i] ^ Long.parseLong(hexBlock[i][col], 16);
                    values[i] = byTwoMultiply(values[i]);
                    values[i] = values[i] ^ Long.parseLong(hexBlock[i][col], 16);
                    values[i] = byTwoMultiply(values[i]);
                    break;
            }
        }
        hexResult = Long.toHexString(values[0] ^ values[1] ^ values[2] ^ values[3]).toUpperCase();

        while (hexResult.length() > 2) {
            StringBuilder hexSb = new StringBuilder(hexResult);
            hexSb.deleteCharAt(0);
            hexResult = hexSb.toString();
        }

        if (hexResult.length() == 1) {
            hexResult = "0" + hexResult;
        }

        return hexResult;
    }
}
