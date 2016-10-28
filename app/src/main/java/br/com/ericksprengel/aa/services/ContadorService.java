package br.com.ericksprengel.aa.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ContadorService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private Thread mThreadContador;
    private ContadorBinder mBinder = new ContadorBinder();
    private Contador mContador;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(BuildConfig.LOG_TAG, "ContadorService: onCreate (" + Thread.currentThread().getId() + ")");
        mContador = new Contador();

        // iniciando thread com o contador
        mThreadContador = new Thread(mContador);
        mThreadContador.start();

        // exibindo a notificação fixa na barra de notificações
        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(BuildConfig.LOG_TAG, "ContadorService: onStartCommand (" + Thread.currentThread().getId() + ")");

        return START_STICKY; // super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(BuildConfig.LOG_TAG, "ContadorService: onBind (" + Thread.currentThread().getId() + ")");

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(BuildConfig.LOG_TAG, "ContadorService: onUnbind (" + Thread.currentThread().getId() + ")");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onCreate();
        Log.d(BuildConfig.LOG_TAG, "ContadorService: onDestroy (" + Thread.currentThread().getId() + ")");
        mContador.pararContagem();

        // removendo notificação
        stopForeground(true);
    }

    /**
     * Binder
     */
    public class ContadorBinder extends Binder {
        ContadorService getService() {
            return ContadorService.this;
        }
    }

    /**
     * Notificação
     */
    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Ticker.")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Título")
                .setContentText("Texto.")  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        startForeground(NOTIFICATION_ID, notification);
    }


    /**
     * Contador
     */

    public int getContagem() {
        if(this.mContador == null) {
            return -1;
        }
        return this.mContador.mContagem;
    }

    class Contador implements Runnable {
        private boolean mContadorHabilitado = false; //TODO: utilizar métodos da Thread para parar a execução (interrupt() e isInterrupted()).
        private int mContagem = 0;

        private void contar() throws InterruptedException {
            mContadorHabilitado = true;
            while (mContadorHabilitado) {
                Thread.sleep(1000);
                mContagem++;
                Log.d(BuildConfig.LOG_TAG, "ContadorService: contando: " + mContagem + "(" + Thread.currentThread().getId() + ")");

            }
        }

        private void pararContagem() {
            this.mContadorHabilitado = false;
        }

        @Override
        public void run() {
            try {
                contar();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
