package org.esisar.command_control.encrypt;

import java.util.HashMap;

public class AES {
    public HashMap<Character,Character>S_box_corr = new HashMap<>();
    public int[][][] matrice_etat;
    public char[] keyc;
    public AES() {
        String key="azertyuiopqsdfgh";

        keyc= new char[16];
        for (int i = 0; i < keyc.length; i++) {
            keyc[i]=key.substring(i,i+1).charAt(0);
            //System.out.println(keyc[i]);
        }
    }

    public String encrypt(String plaintext) {
        // 1. Appel de tes méthodes dans l'ordre du tour AES
        formate(plaintext);
        addRoundKey();
        subBytes();
        shiftRows();
        mixColums();

        // 2. Reconstruction du String à partir de la matrice d'état
        // On va transformer chaque nombre en sa représentation Hexadécimale (00 à FF)
        StringBuilder ciphertext = new StringBuilder();

        for (int i = 0; i < matrice_etat.length; i++) {
            // On parcourt exactement comme dans ta méthode formate()
            // pour garder l'ordre des données
            for (int ligne = 0; ligne < 4; ligne++) {
                for (int col = 0; col < 4; col++) {
                    int val = matrice_etat[i][col][ligne];
                    // On formate en Hexa sur 2 caractères (ex: 10 -> "0A")
                    ciphertext.append(String.format("%02x", val));
                }
            }
        }

        return ciphertext.toString();
    }


    private void setS_box_corr() {
        char[] tab= {99,124,119,123,242,107,111,197,48,1,103,43,254,215,171,118,202,130,201,125,250,89,71,240,173,212,162,175,156,164,114,192
                ,183,253,147,38,54,63,247,204,52,165,229,241,113,216,49,21,4,199,35,195,24,150,5,154,7,18,128,226,235,39,178,117,9,131,44,26,27,
                110,90,160,82,59,214,179,41,227,47,132,83,209,0,237,32,252,177,91,106,203,190,57,74,76,88,207,208 ,239 ,170 ,251 ,67,77,51,
                133 ,69 ,249 ,2 ,127, 80, 60, 159, 168, 81, 163, 64, 143, 146, 157, 56, 245, 188, 182, 218, 33, 16, 255, 243, 210,
                205, 12, 19, 236, 95, 151, 68, 23, 196, 167, 126 ,61 ,100 ,93 ,25 ,115 ,96 ,81 ,79 ,220 ,34 ,42 ,144 ,
                136 ,70 ,238 ,184 ,20 ,222, 94 ,11 ,219 ,224 ,50 ,58 ,10 ,73 ,6, 36, 92, 194, 211, 172, 98 ,145, 149, 228, 121,
                231, 200, 55 ,109 ,141 ,213 ,78 ,169 ,108 ,86 ,244 ,234, 101, 122, 174, 8 ,186 ,120 ,37 ,46 ,28 ,166 ,180 ,198 ,232 ,221,
                116, 31 ,75 ,189, 139, 138, 112, 62, 181, 102, 72, 3, 246, 14 ,97, 53, 87, 185, 134, 193, 29, 158, 225, 248, 152, 17 ,105, 217,
                142, 148, 155, 30, 135, 233, 206, 85, 40, 223, 140, 161, 137, 13 ,191, 230, 66, 104, 65,
                153, 45, 15, 176, 84, 187, 22
        };// etc... mdrr
        int index =0;
        for(int i=0;i<256;i++) {

            if(i==16) {
                System.out.println("tabindex: "+ (int)tab[index]);

            }
            S_box_corr.put((char)i, tab[index]);
            index++;

        }

    }


    public void formate(String text) {
        // String into ascii
        String[] letter;
        letter= new String[text.length()];
        for(int i=0; i<text.length(); i++) {
            letter[i]= text.substring(i,i+1);

        }
        int[] hexa;
        int taille =((letter.length/16) +1)*16;
        hexa= new int[taille];
        for(int i=0; i<taille; i++) {
            if(i<letter.length) {
                hexa[i]=letter[i].charAt(0);

            }else{
                hexa[i]=taille-letter.length;

            }


        }

        matrice_etat= new int[taille/16][4][4];
        for (int i = 0; i < taille/16; i++) {
            int index_col=0;
            int index_ligne=0;
            for (int k =i*16;k<16*(i+1);k++) {
                matrice_etat[i][index_col][index_ligne]= hexa[k];
                index_col++;
                if(index_col==4) {
                    index_col=0;
                    index_ligne++;
                }

            }

        }
        //test





    }
    public int[][] copyTab(int[][] tab) {
        int[][] tabcopy =  new int[tab.length][tab[0].length];
        for (int i = 0; i < tabcopy.length; i++) {
            for (int j = 0; j < tabcopy[0].length; j++) {
                tabcopy[i][j]= tab[i][j];

            }

        }
        return tabcopy;
    }
    private void shiftRows(){
        for (int i = 0; i < matrice_etat.length; i++) {

            int[][] matrice_etat_temp = copyTab(matrice_etat[i]);
            for(int k=1;k<4;k++) {
                int[] tab= matrice_etat[i][k];
                for(int l=0;l<4;l++) {
                    //ligne colonne
                    matrice_etat[i][k][l]=matrice_etat_temp[k][ (l+k)%4  ];


                }


            }




        }




    }
    private void addRoundKey() {

        for (int i = 0; i < matrice_etat.length; i++) {
            int index_col=0;
            int index_ligne=0;
            for (int k =i*16;k<16*(i+1);k++) {
                matrice_etat[i][index_col][index_ligne]= matrice_etat[i][index_col][index_ligne]^keyc[k];
                index_col++;
                if(index_col==4) {
                    index_col=0;
                    index_ligne++;
                }

            }
        }



    }
    private void subBytes() {
        for (int i = 0; i < matrice_etat.length; i++) {
            int index_col=0;
            int index_ligne=0;
            for (int k =i*16;k<16*(i+1);k++) {
                int valeur =matrice_etat[i][index_col][index_ligne];

                matrice_etat[i][index_col][index_ligne]= S_box_corr.get((char)valeur);
                index_col++;
                if(index_col==4) {
                    index_col=0;
                    index_ligne++;
                }

            }
        }

    }
    private void mixColums() {
        int[][] matrice_galois= {
                {02,  03,  01,  01},
                {01,  02,  03,  01},
                {01 , 01 , 02 , 03},
                {03 , 01 , 01 , 02} 		};
        for(int i=0; i<matrice_etat.length;i++) {

            for(int j=0;j<4;j++) {
                for(int k=0;k<4;k++) {
                    // extraire colums!!!!

                }
            }
        }

    }
    private int[][] mult(int [][]mat1,int[][]mat2){
        int[][]result;
        result= new int[mat1.length][mat2[0].length];
        for(int i=0;i<mat1.length;i++) {
            for(int j=0;j<mat2[0].length;j++) {
                int sum=0;
                for(int k=0;k<mat2.length;k++) {
                    sum+=mat1[i][k]*mat2[k][j];

                }
                result[i][j]=sum;
                System.out.print(result[i][j]+" ");
            }
            System.out.println();
        }

        return result;
    }
    public void affiche_matrice_etat() {
        for (int i = 0; i < matrice_etat.length; i++) {
            System.out.println("--- Bloc n°" + i + " ---");

            // On parcourt les lignes du bloc
            for (int j = 0; j < matrice_etat[i].length; j++) {

                // On parcourt les colonnes de la ligne
                for (int k = 0; k < matrice_etat[i][j].length; k++) {
                    // Affichage de l'élément avec une tabulation pour l'alignement
                    System.out.print(matrice_etat[i][j][k] + "\t");
                }
                // Saut de ligne après chaque ligne du bloc
                System.out.println();
            }
            // Saut de ligne entre chaque bloc pour la lisibilité
            System.out.println();
        }
    }
}