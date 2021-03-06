package entity;
// Generated 6 f�vr. 2016 21:43:55 by Hibernate Tools 3.6.0

import __main__.GlobalMethods;
import gui.warehouse_dispatch.state.WarehouseHelper;
import hibernate.DAO;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 * HisPalletPrint generated by hbm2java
 */
@Entity
@Table(name = "dispatch_note")
public class DispatchNote extends DAO implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dispatch_id_seq")
    @SequenceGenerator(name = "dispatch_id_seq", sequenceName = "dispatch_id_seq", allocationSize = 1)
    private Integer id;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    private Date createTime;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "create_id")
    private int createId;

    @Column(name = "advice_note_num")
    private String adviceNoteNum;    

    @Column(name = "m_user")
    private String user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dispatchNote", cascade = CascadeType.ALL)
    private final Set<DispatchNoteLine> noteLines = new HashSet<DispatchNoteLine>(0);
       
    public DispatchNote() {
    }

    @Override
    public String toString() {
        return "DispatchNote{" + "id=" + id + ", createTime=" + createTime + ", createId=" + createId + ", adviceNote=" + adviceNoteNum + ", user=" + user + ", noteLines=" + noteLines + '}';
    }

    public DispatchNote(Date createTime, int createId, String adviceNote, String user) {
        this.createTime = GlobalMethods.getTimeStamp(null);
        this.createId = WarehouseHelper.warehouse_reserv_context.getUser().getId();
        this.adviceNoteNum = adviceNote;        
        this.user = user;
    }

    public String getCreateTimeString(String format) {
        if (format == null) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        DateFormat df = new SimpleDateFormat(format);
        return (df.format(this.createTime));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    
    
    public int getCreateId() {
        return createId;
    }

    public void setCreateId(int createId) {
        this.createId = createId;
    }

    public String getAdviceNoteNum() {
        return adviceNoteNum;
    }

    public void setAdviceNoteNum(String adviceNoteNum) {
        this.adviceNoteNum = adviceNoteNum;
    }
    
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
