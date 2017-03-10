package com.chenmo.pintugame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class MainActivity extends Activity {

    private GamePIcLayout game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        game = (GamePIcLayout) findViewById(R.id.game);
        game.setOnGamePintuListener(new GamePIcLayout.GamePintuListener() {
            @Override
            public void nextLevel(final int nextLevel) {
                new AlertDialog.Builder(MainActivity.this).setTitle("是否进入下一关").setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        game.nextLevel();
                    }
                }).
                        setNegativeButton("否", null).
                        show();
            }

            @Override
            public void timeChanged(int currentTime) {

            }

            @Override
            public void gameOver() {

            }
        });
    }
}
