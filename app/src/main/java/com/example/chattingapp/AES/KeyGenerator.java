package com.example.chattingapp.AES;

import java.util.*;

public class KeyGenerator {
    String key;

    public KeyGenerator(String key) {
        this.key = key;
    }

    private static List<String> gFunction(List<String> lastCol) {
//        Rot Word Operation

        String temp = lastCol.get(0);
        for (int i = 1; i < lastCol.size(); i++) {
            lastCol.set((i - 1), lastCol.get(i));
        }
        lastCol.set(3, temp);

//        SubBytes Operation
        for (int i = 0; i < 4; i++) {
            String a = lastCol.get(i);
            int r = 0;
            int c = 0;
            if (a.length() == 2) {
                r = Integer.parseInt(String.valueOf(a.charAt(0)), 16);
                c = Integer.parseInt(String.valueOf(a.charAt(1)), 16);
            } else {
                r = Integer.parseInt("0", 16);
                c = Integer.parseInt(String.valueOf(a.charAt(0)), 16);
            }

            lastCol.set(i, Table.Sbox[r][c].toUpperCase());
        }

        return lastCol;
    }


    public Map<Integer, List<String>> mainFunction() {

        String[] keyHex = new String[16];

        //Generate The Hex of Key and stored in keyHex
        for (int i = 0; i < key.length(); i++) {
            keyHex[i] = Integer.toHexString((int) key.charAt(i)).toUpperCase();
        }

//        System.out.println(" Key Hex Value : " + Arrays.toString(keyHex));

//        @KeyState
//        Add to Metrix
//        [
//        54,53,50,31
//        45 43 49 32
//        41 4F 41 33
//        4D 52 4E 34
//        ]
        int pos = -1;
        String[][] keyMetrix = new String[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                pos++;
                keyMetrix[j][i] = keyHex[pos];
            }
        }

        List<String> lastCol = new ArrayList<>();
        String[][] keys = new String[4][4];
        HashMap<Integer, List<String>> map = new HashMap<>();
        int count = 0;

        map.put(count, Arrays.asList(keyHex));
        count++;

        for (int i = 0; i < 10; i++) {


            for (int j = 0; j < 4; j++) {

                lastCol.add(keyMetrix[j][3]);
            }
            lastCol = gFunction(lastCol);

            keys = calculateKey(lastCol, i, keyMetrix);

            for (int p = 0; p < 4; p++) {
                for (int q = 0; q < 4; q++) {
                    keyMetrix[p][q] = keys[p][q];
                }
            }
            ArrayList<String> copy = new ArrayList<>();
            for (int p = 0; p < 4; p++) {
                for (int q = 0; q < 4; q++) {
                    copy.add(keys[q][p]);
                }
            }
            map.put(count, copy);
            count++;


            lastCol.clear();
        }

        return map;
    }

    private static String[][] calculateKey(List<String> lastCol, int pos, String[][] keyMetrix) {
        int temp = 0;
        String ans[][] = new String[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == 0) {
                    temp = Integer.parseInt(keyMetrix[j][i], 16) ^ Integer.parseInt(lastCol.get(j), 16);
                    temp = temp ^ Integer.parseInt(Table.RCON[j][pos], 16);
                    ans[j][i] = Integer.toHexString(temp).toUpperCase();
                } else {
                    temp = Integer.parseInt(keyMetrix[j][i], 16) ^
                            Integer.parseInt(ans[j][i - 1], 16);
                    ans[j][i] = Integer.toHexString(temp).toUpperCase();
                }
            }
        }
//        System.out.println("Keys " + (pos + 1) + "  " + Arrays.deepToString(ans));
        return ans;

    }
}


