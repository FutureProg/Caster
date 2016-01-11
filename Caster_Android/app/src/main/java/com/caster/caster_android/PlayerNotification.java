package com.caster.caster_android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by Nick on 15-08-03.
 */
public class PlayerNotification extends Notification{


    //TODO: IMPLEMENT THIS SHIT
    public static int NOTIFICATION_ID = 32457567;

    private Context ctx;
    private NotificationManager notificationManager;

    public PlayerNotification(Context ctx){
        super();
        this.ctx = ctx;
        String ns = Context.NOTIFICATION_SERVICE;
        notificationManager = (NotificationManager) ctx.getSystemService(ns);
        CharSequence tickerText = "Shortcuts";
        long when = System.currentTimeMillis();
        RemoteViews contentView = new RemoteViews(ctx.getPackageName(),
                R.layout.player_notification_layout);
        contentView.setImageViewBitmap(R.id.podcast_image,PodcastPlayer.podcast.getCoverPhoto());
        contentView.setTextViewText(R.id.podcast_title,PodcastPlayer.podcast.getTitle());
        Notification.Builder builder = new Notification.Builder(ctx);
        Notification notif = builder.setWhen(when).setTicker(tickerText)
                .setSmallIcon(R.drawable.ic_caster_logo).build();
        notif.contentView = contentView;
        notif.flags |= Notification.FLAG_ONGOING_EVENT;
        CharSequence contentTitle = "From Shortcuts";
        setListeners(contentView);
        notificationManager.notify(NOTIFICATION_ID,notif);
    }

    public void setListeners(RemoteViews view){
        /*Intent intent = new Intent(ctx,HelperActivity.class);
        intent.putExtra("DO","PLAY");
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx,0,intent,0);
        view.setOnClickPendingIntent(R.id.playButton,pendingIntent);*/

        Intent intent = new Intent("com.caster.caster_android.ACTION_PLAY");
        intent.putExtra("VIEW",view);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,100,intent,0);
        view.setOnClickPendingIntent(R.id.playButton,pendingIntent);
        view.setOnClickPendingIntent(R.id.nextButton,pendingIntent);
    }

}
