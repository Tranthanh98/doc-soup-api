package logixtek.docsoup.api.features.share.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Permission {
    static final int read= 10;
    static final int write =20;

    public  static Permission ofWrite()
    {
        return  Permission.of(write);
    }

    public  static Permission ofRead()
    {
        return  Permission.of(read);
    }

    public  static Permission ofDeny()
    {
        return  Permission.of(0);
    }

    @Getter
    @Setter
    Integer value;

    public  boolean canRead()
    {
        return  value == read || value ==write;
    }

    public boolean canWrite()
    {
        return  value ==write;
    }

    public boolean isDenied()
    {
        return  value !=write && value!=read;
    }
}
