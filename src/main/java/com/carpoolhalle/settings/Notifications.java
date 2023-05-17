package com.carpoolhalle.settings;

import com.carpoolhalle.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Notifications {

    private boolean carpoolUpdatedByEmail;
    private boolean carpoolUpdatedByWeb;

    public Notifications(Account account){
        this.carpoolUpdatedByEmail    = account.isCarpoolUpdatedByEmail();
        this.carpoolUpdatedByWeb      = account.isCarpoolUpdatedByWeb();
    }

}
