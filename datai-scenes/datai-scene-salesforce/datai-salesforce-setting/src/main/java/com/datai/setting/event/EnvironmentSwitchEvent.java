package com.datai.setting.event;

import org.springframework.context.ApplicationEvent;

import com.datai.setting.domain.DataiConfigEnvironment;

/**
 * 环境切换事件
 * 当系统环境发生切换时触发此事件
 *
 * @author datai
 * @date 2025-12-25
 */
public class EnvironmentSwitchEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final DataiConfigEnvironment oldEnvironment;
    private final DataiConfigEnvironment newEnvironment;
    private final String switchReason;

    public EnvironmentSwitchEvent(Object source, DataiConfigEnvironment oldEnvironment, 
                                   DataiConfigEnvironment newEnvironment, String switchReason) {
        super(source);
        this.oldEnvironment = oldEnvironment;
        this.newEnvironment = newEnvironment;
        this.switchReason = switchReason;
    }

    public DataiConfigEnvironment getOldEnvironment() {
        return oldEnvironment;
    }

    public DataiConfigEnvironment getNewEnvironment() {
        return newEnvironment;
    }

    public String getSwitchReason() {
        return switchReason;
    }

    @Override
    public String toString() {
        return "EnvironmentSwitchEvent{" +
                "oldEnvironment=" + (oldEnvironment != null ? oldEnvironment.getEnvironmentCode() : "null") +
                ", newEnvironment=" + (newEnvironment != null ? newEnvironment.getEnvironmentCode() : "null") +
                ", switchReason='" + switchReason + '\'' +
                '}';
    }
}
