package com.example.chattingapp.AES;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Encryption {
    public static String key;
    public static String plainText;
    public static List<String> final_encrypted = new ArrayList<>();


    public Encryption(String key, String plain_text) throws UnsupportedEncodingException {
        this.key = key;
        plainText = plain_text;
        getAllKeys();
    }

    private static void getAllKeys() throws UnsupportedEncodingException {

        KeyGenerator k = new KeyGenerator(key);
        Map<Integer, List<String>> keys = k.mainFunction();

        byte[] myBytes = plainText.getBytes("UTF-8");
        plainText = "";
        for (byte b : myBytes) {
            String st = String.format("%2X", b);
            plainText += st;
        }

        while (plainText.length() % 32 != 0) {
            plainText = "0" + plainText;
        }

        System.out.println(plainText + "    " + plainText.length());
        String temp = "";
        for (int i = 0; i < plainText.length(); i += 32) {

            temp = plainText.substring(i, i + 32);

//            System.out.println(temp);
            doEncryption(temp, keys);

        }

    }

    private static void doEncryption(String plainText, Map<Integer, List<String>> keys) {
        //  Add  Roundkey
        List<String> ptR = new ArrayList<>();
        for (int i = 0; i < plainText.length() - 1; i += 2) {
            ptR.add(plainText.substring(i, i + 2));
        }

//        System.out.println(pt);

        List<String> keyR = keys.get(0);

        List<String> afR = new ArrayList<>();

//        System.out.println("Key : " + keyR);
//        System.out.println("Text : " + plainText);

        for (int i = 0; i < keyR.size(); i++) {
            int result = Integer.parseInt(ptR.get(i), 16) ^ Integer.parseInt(keyR.get(i), 16);
            String s = Integer.toHexString(result).toUpperCase();
            if (s.length() < 2) {
                s = "0" + s;
            }
            afR.add(i, s);
        }
//        System.out.println("After R : "+afR);

//        Round Operations
        System.out.println(keys.size());
        for (int i = 1; i < keys.size(); i++) {
            if (i == 10) {
                afR = subBytes(afR);
//                System.out.println("Before Shifting : " + afR);
                String nowMix[][] = shiftRows(afR);
//                System.out.println("After shifting : " + Arrays.deepToString(nowMix));
                afR.clear();

                for (int j = 0; j < 4; j++) {
                    for (int k = 0; k < 4; k++) {
                        afR.add(nowMix[k][j]);
                    }
                }


            } else {
                afR = subBytes(afR);
//                System.out.println("Before Shifting : " + afR);

                String nowMix[][] = shiftRows(afR);
//                System.out.println("After shifting : " + Arrays.deepToString(nowMix));

                nowMix = mixColumn(nowMix);
                afR.clear();
                for (int j = 0; j < 4; j++) {
                    for (int k = 0; k < 4; k++) {
                        afR.add(nowMix[k][j]);
                    }
                }
            }

            keyR = keys.get(i);

            List<String> repeatedafR = new ArrayList<>();

            for (int p = 0; p < afR.size(); p++) {
                int result = Integer.parseInt(keyR.get(p), 16) ^ Integer.parseInt(afR.get(p), 16);
                String s = Integer.toHexString(result).toUpperCase();
                if (s.length() < 2) {
                    s = "0" + s;
                }
                repeatedafR.add(p, s);
            }
            afR = repeatedafR;

            if (i == 10) {
                final_encrypted.addAll(repeatedafR);
            }

        }
//        System.out.println("Ans : "+final_encrypted);


    }

    private static String[][] mixColumn(String[][] nowMix) {
        int fix[][] = {
                {2, 3, 1, 1},
                {1, 2, 3, 1},
                {1, 1, 2, 3},
                {3, 1, 1, 2},
        };
        String[][] ans = new String[4][4];
        int temp = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                ArrayList<Integer> val = new ArrayList<>();
                for (int k = 0; k < 4; k++) {
//                    System.out.print(fix[i][k] + " " + nowMix[k][j]);
                    if (fix[i][k] == 3) {
                        temp = mixvalueforTHree(fix[i][k], Integer.parseInt(nowMix[k][j], 16));

                    } else {
                        temp = findmixValue((fix[i][k] * Integer.parseInt(nowMix[k][j], 16)));
                    }
                    val.add(temp);
                }

//                /XOR Operation
                temp = val.get(0) ^ val.get(1) ^ val.get(2) ^ val.get(3);

                ans[i][j] = Integer.toHexString(temp).toUpperCase();
            }

        }

        return ans;
    }

    private static int findmixValue(int val) {
        int mix = val;
        String modi = findMod(val);
        return Integer.parseInt(modi, 2);
    }

    private static int mixvalueforTHree(int fix, int value) {
        int temp = 2 * value;
        String mod = findMod(temp);
        int con = Integer.parseInt(mod, 2);
        con = con ^ value;

//        System.out.println("con : "+Integer.toHexString(con));


        return con;

    }

    private static String findMod(int temp) {
        String a1 = Integer.toBinaryString(temp);
        String b1 = "100011011";
        String mode = "";

        List<Integer> a = new ArrayList<>();
        for (int i = 0; i < a1.length(); i++) {
            a.add(Integer.parseInt(String.valueOf(a1.charAt(i))));
        }

        List<Integer> b = new ArrayList<>();
        for (int i = 0; i < b1.length(); i++) {
            b.add(Integer.parseInt(String.valueOf(b1.charAt(i))));
        }
        while (b.size() <= a.size()) {
            if (a.get(0) == 1) {
                a.remove(0);
            }

            for (int j = 0; j < b.size() - 1; j++) {
                a.set(j, (a.get(j) ^ b.get(j + 1)));
            }

        }
        for (Integer i : a) {
            mode += String.valueOf(i);
        }
        return mode;

    }


    private static String[][] shiftRows(List<String> afR) {
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
            for (int j = 0; j < 4; j++) {
                int a = j - shift;
                if (a < 0) {
                    a += 4;
                }
                key[a] = ans[i][j];
            }
            for (int j = 0; j < 4; j++) {
                ans[i][j] = key[j];
            }
        }
        return ans;
    }

    private static List<String> subBytes(List<String> afR) {
        for (int i = 0; i < afR.size(); i++) {
            String a = afR.get(i);
            int r = 0;
            int c = 0;
            r = Integer.parseInt(String.valueOf(a.charAt(0)), 16);
            c = Integer.parseInt(String.valueOf(a.charAt(1)), 16);

            afR.set(i, Table.Sbox[r][c].toUpperCase());

        }
        return afR;
    }
}
