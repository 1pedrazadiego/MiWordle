package es.riberadeltajo.miwordle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout linearLayout;
    GridLayout glSuperior;
    GridLayout glTeclado;
    final int NUMERO_COLUMNAS=5;
    final int NUMERO_FILAS=6;
    final int TECLADO_COLUMNAS=10;
    final int TECLADO_FILAS=3;
    final int DICCIONARIO=3898;
    final int VERDE= Color.rgb(0,255,0);
    final int AMARILLO=Color.rgb(255,255,0);
    final int OSCURO=Color.rgb(50,50,50);
    boolean fin_intento=false;
    int intento=0;
    int puntero_columna=0;
    public String palabra;
    ImageButton borrar, enter;
    Character letras[][]=new Character[][]{
            {'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P'},
            {'A','S','D','F','G','H','J','K','L','Ñ'},
            {'Z','X','C','V','B','N','M'}};
    Button [][]botonesJuego=new Button[NUMERO_FILAS][NUMERO_COLUMNAS];
    Button [][]botonesTeclado=new Button[TECLADO_FILAS][TECLADO_COLUMNAS];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout=findViewById(R.id.linearLayout);
        linearLayout.post(new Runnable() {
            @Override
            public void run() { anadirBotones();}
        });
        //GENERAMOS UNA PALABRA ALEATORIA
        palabra=palabraAleatoria("palabras5.txt");
    }


    private void anadirBotones() {

        glSuperior=findViewById(R.id.glSuperior);
        ViewGroup.LayoutParams paramsSuperior=
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, linearLayout.getHeight()/2);
        glSuperior.setLayoutParams(paramsSuperior);
        glSuperior.setColumnCount(NUMERO_COLUMNAS);
        glSuperior.setRowCount(NUMERO_FILAS);


        for (int i = 0; i < NUMERO_FILAS; i++) {
            for (int j = 0; j < NUMERO_COLUMNAS; j++) {
                botonesJuego[i][j]=anadirBoton(glSuperior,"",false,
                        linearLayout.getWidth()/NUMERO_COLUMNAS,linearLayout.getHeight()/2/NUMERO_FILAS);
            }
        }


        glTeclado=findViewById(R.id.glTeclado);
        glTeclado.setColumnCount(TECLADO_COLUMNAS);
        glTeclado.setRowCount(TECLADO_FILAS);

        for (int i = 0; i < TECLADO_FILAS; i++) {
            for (int j = 0; j < letras[i].length; j++) {
                Button b = anadirBoton(glTeclado, letras[i][j].toString(), true,
                        linearLayout.getWidth() / TECLADO_COLUMNAS, linearLayout.getHeight() / 4 / TECLADO_FILAS);
                b.setOnClickListener(this);
            }
        }

        //AGREGAMOS EL BOTÓN PARA BORRAR
        borrar=new ImageButton(this);
        borrar.setLayoutParams(new LinearLayout.LayoutParams(linearLayout.getWidth() / TECLADO_COLUMNAS, linearLayout.getHeight() / 4 / TECLADO_FILAS));
        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fin_intento) return;
                puntero_columna--;
                if(puntero_columna<0){
                    puntero_columna=0;
                }
                botonesJuego[intento][puntero_columna].setText("");
            }
        });
        borrar.setImageResource(R.drawable.ic_baseline_backspace_24);
        glTeclado.addView(borrar);

        //AGREGAMOS EL BOTON ENTER
        enter=new ImageButton(this);
        ViewGroup.LayoutParams parametrosEnter=new ViewGroup.LayoutParams(linearLayout.getWidth() / TECLADO_COLUMNAS,
                linearLayout.getHeight() / 4 / TECLADO_FILAS);
        enter.setLayoutParams(parametrosEnter);
        enter.setImageResource(R.drawable.ic_baseline_send_24);
        glTeclado.addView(enter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fin_intento) return;
                if(puntero_columna<5) {
                    Toast.makeText(getApplicationContext(), "LA PALABRA NO ESTÁ COMPLETA", Toast.LENGTH_LONG).show();
                    return;
                }
                byte[] b=new byte[5];
                Button [][] btn=new Button[TECLADO_FILAS][TECLADO_COLUMNAS];
                for (int i = 0; i < 5; i++) {
                    b[i]=botonesJuego[intento][i].getText().toString().getBytes()[0];
                    if (b[i]==palabra.charAt(i)){
                        botonesJuego[intento][i].setBackgroundColor(VERDE);
                        /*for (int j = 0; j < TECLADO_FILAS; j++) {
                            for (int k = 0; k < letras[j].length; k++) {
                                cambiaVerde(btn[j][k]);
                            }
                        }*/
                    }else{
                        String prueba= ""+(char)b[i];
                        if(palabra.contains(prueba)){
                            botonesJuego[intento][i].setBackgroundColor(AMARILLO);
                            /*for (int j = 0; j < TECLADO_FILAS; j++) {
                                for (int k = 0; k < letras[j].length; k++) {
                                    cambiaAmarillo(btn[j][k]);
                                }
                            }*/
                        }/*else{
                            for (int j = 0; j < TECLADO_FILAS; j++) {
                                for (int k = 0; k < letras[j].length; k++) {
                                    cambiaOscuro(btn[j][k]);
                                }
                            }
                        }*/
                    }
                }

                String palabraIntentada=new String(b);
                if(palabraIntentada.equals(palabra)){
                    Toast.makeText(getApplicationContext(), "¡CORRECTO!", Toast.LENGTH_LONG).show();
                    fin_intento=true;
                }else{
                    intento++;
                    puntero_columna=0;
                    if(intento>5){
                        Toast.makeText(getApplicationContext(), "¡NO TE QUEDAN MÁS INTENTO! ¡LA PALABRA ERA "+palabra, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void cambiaOscuro(Button b) {
        b.setBackgroundColor(OSCURO);
    }

    private void cambiaAmarillo(Button b) {
        b.setBackgroundColor(AMARILLO);
    }

    private void cambiaVerde(Button b) {
        b.setBackgroundColor(VERDE);
    }

    public Button anadirBoton(GridLayout g,String texto,boolean clickable, int width, int height){
        Button b=new Button(this);
        ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(width,height);
        b.setClickable(clickable);
        b.setText(texto);
        b.setLayoutParams(params);
        g.addView(b);
        return b;
    }

    @Override
    public void onClick(View view) {
        if(fin_intento)
            return;
        Button btn=(Button) view;
        if (puntero_columna<5){
            botonesJuego[intento][puntero_columna].setText(btn.getText());
            puntero_columna++;
        }
    }

    //METODO PARA GENERAR UNA PALABRA ALEATORIA DEL ARCHIVO PALABRAS5.TXT
    public String palabraAleatoria(String filename){
        byte [] b=new byte[5];
        Random palabra=new Random();
        try {
            InputStream i=getAssets().open(filename, AssetManager.ACCESS_RANDOM);
            long nAleatorio=palabra.nextInt(DICCIONARIO);
            i.skip(nAleatorio*6); //*6 porque contamos el salto de línea de palabras5.txt
            i.read(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(b).toUpperCase();
    }
}