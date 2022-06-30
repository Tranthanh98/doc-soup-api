package logixtek.docsoup.api.features.share.services;

import logixtek.docsoup.api.features.share.models.Permission;
import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.entities.*;

public interface PermissionService {
    Permission get(OwnerInfo owner, BaseIdentityCommand<?> request);

    Permission getOfFile(FileEntity file,BaseIdentityCommand<?> request);

    Permission getOfContact(ContactEntity file, BaseIdentityCommand<?> request);

    Permission getOfDataRoom(DataRoomEntity dataRoom, BaseIdentityCommand<?> request);

    Permission getOfLink(LinkEntity link, BaseIdentityCommand<?> request);
}
