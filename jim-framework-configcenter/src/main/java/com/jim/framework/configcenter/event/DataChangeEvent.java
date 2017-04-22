package com.jim.framework.configcenter.event;

import java.util.Map;

public class DataChangeEvent {

    private Map<String,Object> changedData;

    private ChangeType changeType;

    public DataChangeEvent(Map<String,Object> changedData) {
        this.changedData=changedData;
        this.changeType=ChangeType.MODIFY;
    }

    public DataChangeEvent(Map<String,Object> changedData,ChangeType changeType) {
        this.changedData=changedData;
        this.changeType=changeType;
    }
    public Map<String, Object> getChangedData() {
        return changedData;
    }

    public ChangeType getChangeType() {
        return changeType;
    }


    public enum ChangeType{
        DELETE(1),CREATE(2),MODIFY(3);
        private int type;
        public int getType(){
            return this.type;
        }
        private ChangeType(int type){
            this.type=type;
        }
    }
}

