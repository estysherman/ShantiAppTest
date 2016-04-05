package webit.android.shanti.general.gcm;

import webit.android.shanti.entities.KeyValue;

import java.util.List;

/**
 * Created by CRM on 19/03/2015.
 */
public class MessageToSend {

    private String sMessage;
    private List<KeyValue> ParamsList;
    private List<Integer> lUsers;

    public MessageToSend(String sMessage, List<KeyValue> paramsList, List<Integer> lUsers) {
        this.sMessage = sMessage;
        this.ParamsList = paramsList;
        this.lUsers = lUsers;
    }

    public String getsMessage() {
        return sMessage;
    }

    public void setsMessage(String sMessage) {
        this.sMessage = sMessage;
    }

    public List<Integer> getlUsers() {
        return lUsers;
    }

    public void setlUsers(List<Integer> lUsers) {
        this.lUsers = lUsers;
    }

    public List<KeyValue> getParamsList() {
        return ParamsList;
    }

    public void setParamsList(List<KeyValue> paramsList) {
        ParamsList = paramsList;
    }
}
