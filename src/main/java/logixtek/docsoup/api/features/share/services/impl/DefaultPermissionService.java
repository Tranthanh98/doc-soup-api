package logixtek.docsoup.api.features.share.services.impl;

import logixtek.docsoup.api.features.share.models.Permission;
import logixtek.docsoup.api.features.share.services.PermissionService;
import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.entities.*;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomRepository;
import logixtek.docsoup.api.infrastructure.repositories.DataRoomUserRepository;
import logixtek.docsoup.api.infrastructure.repositories.DirectoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DefaultPermissionService implements PermissionService {

    private final DirectoryRepository directoryRepository;
    private final DataRoomUserRepository dataRoomUserRepository;
    private final DataRoomRepository dataRoomRepository;

    @Override
    public Permission get(OwnerInfo owner, BaseIdentityCommand<?> request) {

        if(owner.getCompanyId().toString().equals(request.getCompanyId().toString())) {

            if (owner.getAccountId().equals(request.getAccountId())) {
                return Permission.ofWrite();
            }
            
            if(owner instanceof FileEntity) {
                var fileEntity = (FileEntity) owner;
                var directoryOption = directoryRepository.findById(fileEntity.getDirectoryId());
                if(directoryOption.isPresent() && directoryOption.get().getIsTeam()) {
                    return Permission.ofWrite();
                }
            }

            if (owner instanceof CompanyOwnerInfo) {
                var companyInfo = (CompanyOwnerInfo) owner;

                if (companyInfo.getIsTeam() || owner.getAccountId().equals(request.getAccountId())) {
                    return Permission.ofWrite();
                }

            }
        }

        return  Permission.ofDeny();

    }

    @Override
    public Permission getOfFile(FileEntity file, BaseIdentityCommand<?> request) {
        var permission = get(file,request);

        if(permission.isDenied())
        {
            var directoryOption = directoryRepository.findById(file.getDirectoryId());

            if(directoryOption.isPresent())
            {
                return  get(directoryOption.get(),request);
            }
        }

        return permission;
    }

    @Override
    public Permission getOfContact(ContactEntity contact, BaseIdentityCommand<?> request) {
        if(contact.getCompanyId().toString().equals(request.getCompanyId().toString())) {
            if (contact.getAccountId().equals(request.getAccountId())) {
                return Permission.ofWrite();
            }

            return Permission.ofRead();
        }

        return  Permission.ofDeny();
    }

    @Override
    public Permission getOfLink(LinkEntity link, BaseIdentityCommand<?> request) {
        if(link.getDocumentId() == null) {
            var dataRoomOption = dataRoomRepository.findById(link.getRefId());
            if(!dataRoomOption.isPresent()) {
                return Permission.ofDeny();
            }

            return  getOfDataRoom(dataRoomOption.get(), request);
        }

        if(!link.getCompanyId().toString().equals(request.getCompanyId().toString())
                || !link.getCreatedBy().equals(request.getAccountId())) {
            return Permission.ofDeny();
        }

        return  Permission.ofWrite();
    }

    @Override
    public Permission getOfDataRoom(DataRoomEntity dataRoom, BaseIdentityCommand<?> request) {
        if(request.getAccountId().equals(dataRoom.getAccountId())) {
            return Permission.ofWrite();
        }

        var dataRoomUserOption = dataRoomUserRepository.findByUserIdAndDataRoomId(request.getAccountId(), dataRoom.getId());
        if(dataRoomUserOption.isPresent()) {
            return Permission.ofWrite();
        }

        return Permission.ofDeny();
    }

}
