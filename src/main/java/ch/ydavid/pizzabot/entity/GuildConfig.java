package ch.ydavid.pizzabot.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "guildconfig")
public class GuildConfig {

    public GuildConfig() {
    }

    public GuildConfig(String guildId, String newVCId, String categoryID) {
        this.guildId = guildId;
        this.newVCId = newVCId;
        this.categoryID = categoryID;
    }

    @Id
    @GeneratedValue
    private Long id;

    private String guildId;

    /**
     * ID of Channel listening to create new dynamic voice channel
     */
    private String newVCId;

    /**
     * Parent of newVCID
     */
    private String categoryID;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public String getNewVCId() {
        return newVCId;
    }

    public void setNewVCId(String newVCId) {
        this.newVCId = newVCId;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }
}
