package com.safshekan.parkban.utils;

import com.pax.dal.IPicc;
import com.pax.dal.entity.EDetectMode;
import com.pax.dal.entity.EM1KeyType;
import com.pax.dal.entity.PiccCardInfo;
import com.pax.dal.exceptions.PiccDevException;
import com.safshekan.parkban.helper.PrintParkbanApp;


public class IPiccUtils {
    private static IPiccUtils instance;
    private IPicc iPicc;

    private IPiccUtils() {
        iPicc = PrintParkbanApp.getInstance().getIpicc();
    }

    public static synchronized IPiccUtils getInstance() {
        if (instance == null) {
            instance = new IPiccUtils();
        }
        return instance;
    }

    public boolean open() {
        try {
            iPicc.open();
            return true;
        } catch (PiccDevException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean close() {
        try {
            iPicc.close();
            return true;
        } catch (PiccDevException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean m1Auth(EM1KeyType type, byte blockNo, byte[] pwd, byte[] serialNo) {
        try {
            iPicc.m1Auth(type, blockNo, pwd, serialNo);
            return true;
        } catch (PiccDevException e) {
            e.printStackTrace();
            return false;
        }
    }

    public byte[] m1Read(byte blockNo) {
        try {
            return iPicc.m1Read(blockNo);
        } catch (PiccDevException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean m1Write(byte blockNo, byte[] value) {
        try {
            iPicc.m1Write(blockNo, value);
            return true;
        } catch (PiccDevException e) {
            e.printStackTrace();
            return false;
        }
    }


    public PiccCardInfo detect(EDetectMode mode) {
        try {
            return iPicc.detect(mode);
        } catch (PiccDevException e) {
            e.printStackTrace();
            return null;
        }
    }


}
