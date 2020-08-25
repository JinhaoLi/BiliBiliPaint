package com.jil.paintf.network;

import com.jil.paintf.custom.ProgressListener;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * 2020/8/25 15:08
 *
 * @author JIL
 **/
public class ProgressResponseBody extends ResponseBody {
    private BufferedSource bufferedSource;
    private final ResponseBody responseBody;
    private ProgressListener listener;

    public ProgressResponseBody(String url, ResponseBody body) {
        this.responseBody = body;
        listener = ProgressInterceptor.LISTENER_MAP.get(url);
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @NotNull
    @Override
    public BufferedSource source() {
        if(bufferedSource ==null){
            bufferedSource = Okio.buffer(new ProgressSource(responseBody.source()));
        }
        return bufferedSource;
    }

    private class ProgressSource extends ForwardingSource{
        long totalBytesRead = 0;

        int currentProgress;

        public ProgressSource(@NotNull Source delegate) {
            super(delegate);
        }

        @Override
        public long read(@NotNull Buffer sink, long byteCount) throws IOException {
            long bytesRead =super.read(sink, byteCount);
            long fullLength =responseBody.contentLength();
            if(bytesRead==-1){
                totalBytesRead =fullLength;
            }else {
                totalBytesRead+=bytesRead;
            }
            int progress =(int)(100f*totalBytesRead /fullLength);
            if(listener!=null&&progress!=currentProgress){
                //listener不为空 && 进度产生了变化
                listener.onProgress(progress);
            }
            if(listener!=null && totalBytesRead ==fullLength){
                listener=null;
            }
            currentProgress =progress;
            return bytesRead;
        }
    }
}
