/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.pojos.workbench;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;

/**
 * POJO for workbench_setting table.
 *  
 */
@Entity
@Table(name = "workbench_setting")
public class WorkbenchSetting implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @GeneratedValue
    @Column(name = "setting_id")
    private Integer settingId;
    
    @Basic(optional = false)
    @Column(name = "installation_directory")
    private String installationDirectory;

    public Integer getSettingId() {
        return settingId;
    }

    public void setSettingId(Integer settingId) {
        this.settingId = settingId;
    }

    public String getInstallationDirectory() {
        return installationDirectory;
    }

    public void setInstallationDirectory(String installationDirectory) {
        this.installationDirectory = installationDirectory;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(settingId).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!WorkbenchSetting.class.isInstance(obj)) {
            return false;
        }
        
        WorkbenchSetting otherObj = (WorkbenchSetting) obj;
        
        return new EqualsBuilder().append(settingId, otherObj.settingId).isEquals();
    }
}
