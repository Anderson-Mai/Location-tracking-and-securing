package anderson.android.trackingbase;

/**
 * Created by Anderson Mai on 4/15/2017.
 */

public class SendFenceLocationRequest {
    private String m_location_name = "";
    private String m_xposition = "";
    private String m_yposition = "";
    private String m_fence_diameter = "";
    public String m_formatString = "";
    private final String fence_request = "FENCE_REQUEST";

    public SendFenceLocationRequest(String device_code, String x_position, String y_position,  String fence_diameter, String expiration_time) {
        m_location_name = device_code;
        m_xposition = x_position;
        m_yposition = y_position;
        m_fence_diameter = fence_diameter;
        m_formatString = Constants.FENCE_REQUEST + Constants.POUND + m_location_name + Constants.POUND + m_xposition +
                Constants.POUND + m_yposition + Constants.POUND + m_fence_diameter + Constants.POUND + expiration_time;

    }

    public void sendFenceRequest(String text_out, String destination) {
         Utilities.sendText(text_out,destination );
    }
}
