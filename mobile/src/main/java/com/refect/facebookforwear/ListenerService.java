package com.refect.facebookforwear;

import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.refect.facebookforwear.MainActivity;
import com.refect.shared.utils.Utils;

/**
 * Created by anelson on 5/12/15.
 */
public class ListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        FacebookSdk.sdkInitialize(getApplicationContext());

        String messageData = new String(messageEvent.getData());
        String header = messageData.substring(0, 3);
        String payload = messageData.substring(3, messageData.length());

        if(header.equals(Utils.REQUEST_FEED)) {
            MainActivity.meFeedGraphApi(getApplicationContext(), "me");
        } else if(header.equals(Utils.REQUEST_ALBUM)) {
            MainActivity.myAlbums(getApplicationContext());
        } else if(header.equals(Utils.REQUEST_PHOTOS)) {
            MainActivity.myPhotosByAlbum(getApplicationContext(), payload);
        } else if(header.equals(Utils.REQUEST_FEED_PAGINATION)) {
            MainActivity.paginateFeed(getApplicationContext(), payload);
        } else if(header.equals(Utils.REQUEST_POST_LIKE)) {
            MainActivity.likePost(getApplicationContext(), payload);
        }

    }
}
