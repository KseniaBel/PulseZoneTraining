package com.ksenia.pulsezonetraining.history;

import com.ksenia.pulsezonetraining.R;

/**
 * Created by ksenia on 10.02.19.
 */

public class HeaderHistoryItem {
    String period;
    boolean isExpanded;

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public HeaderHistoryItem(String period) {
        this.period = period;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void toggleExpandCollapse() {
        isExpanded = !isExpanded;
    }
}
