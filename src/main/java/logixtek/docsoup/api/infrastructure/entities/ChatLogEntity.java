package logixtek.docsoup.api.infrastructure.entities;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "chat_log")
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ChatLogEntity {
    @Id
    @Type(type = "uuid-char")
    UUID id;

    @Column
    OffsetDateTime startChat;

    @Column(columnDefinition = "nvarchar(400)")
    String property;

    @Column(columnDefinition = "nvarchar(4000)")
    String visitor;

    @Column
    OffsetDateTime endChat;

}
