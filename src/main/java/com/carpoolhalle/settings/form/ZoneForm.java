package com.carpoolhalle.settings.form;

import com.carpoolhalle.domain.Zone;

public class ZoneForm {

    private String zoneName;

    //COUNTY,CITY
    public String getCountyName() {
        return zoneName.substring(0, zoneName.indexOf("/"));
    }
    public String getCityName() {
        return zoneName.substring(zoneName.indexOf("/")+1, zoneName.length()-1);
    }
    public Zone getZone(){
        return Zone.builder()
                .county(this.getCountyName())
                .city(this.getCityName()).build();
    }
}
