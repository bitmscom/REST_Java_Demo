package com.bitms.api.client.bean.sign;

public class StdoutStreamErrorListener extends BufferErrorListener
{
    public void end()
    {
        System.out.print(buffer.toString());
    }
}
