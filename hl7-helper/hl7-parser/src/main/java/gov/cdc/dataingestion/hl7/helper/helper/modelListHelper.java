package gov.cdc.dataingestion.hl7.helper.helper;

import ca.uhn.hl7v2.model.v251.datatype.*;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;

import java.util.ArrayList;

public class modelListHelper {
    public static ArrayList<Xpn> GetXpnList(XPN[] xpns) {
        var lst = new ArrayList<Xpn>();
        for(var data: xpns) {
            Xpn item = new Xpn(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Cx> GetCxList(CX[] cxs) {
        var lst = new ArrayList<Cx>();
        for(var data: cxs) {
            Cx item = new Cx(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Ce> GetCeList(CE[] ces) {
        var lst = new ArrayList<Ce>();
        for(var data: ces) {
            Ce item = new Ce(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Xad> GetXadList(XAD[] messages) {
        var lst = new ArrayList<Xad>();
        for(var data: messages) {
            Xad item = new Xad(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Tq> GetTqList(TQ[] messages) {
        var lst = new ArrayList<Tq>();
        for(var data: messages) {
            Tq item = new Tq(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Xtn> GetXtnList(XTN[] messages) {
        var lst = new ArrayList<Xtn>();
        for(var data: messages) {
            Xtn item = new Xtn(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Xcn> GetXcnList(XCN[] messages) {
        var lst = new ArrayList<Xcn>();
        for(var data: messages) {
            Xcn item = new Xcn(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Xon> GetXonList(XON[] messages) {
        var lst = new ArrayList<Xon>();
        for(var data: messages) {
            Xon item = new Xon(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<String> GetIsStringList(IS[] messages) {
        var lst = new ArrayList<String>();
        for(var data: messages) {
            lst.add(data.getValue());
        }
        return lst;
    }

    public static ArrayList<String> GetFtStringList(FT[] messages) {
        var lst = new ArrayList<String>();
        for(var data: messages) {
            lst.add(data.getValue());
        }
        return lst;
    }

    public static ArrayList<String> GetNmStringList(NM[] messages) {
        var lst = new ArrayList<String>();
        for(var data: messages) {
            lst.add(data.getValue());
        }
        return lst;
    }

    public static ArrayList<String> GetDtStringList(DT[] messages) {
        var lst = new ArrayList<String>();
        for(var data: messages) {
            lst.add(data.getValue());
        }
        return lst;
    }

    public static ArrayList<String> GetStStringList(ST[] messages) {
        var lst = new ArrayList<String>();
        for(var data: messages) {
            lst.add(data.getValue());
        }
        return lst;
    }


    public static ArrayList<Cwe> GetCweList(CWE[] messages) {
        var lst = new ArrayList<Cwe>();
        for(var data: messages) {
            Cwe item = new Cwe(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Fc> GetFcList(FC[] messages) {
        var lst = new ArrayList<Fc>();
        for(var data: messages) {
            Fc item = new Fc(data);
            lst.add(item);
        }
        return lst;
    }

    public static ArrayList<Ts> GetTsList(TS[] messages) {
        var lst = new ArrayList<Ts>();
        for(var data: messages) {
            Ts item = new Ts(data);
            lst.add(item);
        }
        return lst;
    }

}
