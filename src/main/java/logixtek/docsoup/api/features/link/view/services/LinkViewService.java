package logixtek.docsoup.api.features.link.view.services;

import logixtek.docsoup.api.features.link.view.responses.LinkResult;
import logixtek.docsoup.api.infrastructure.entities.LinkEntity;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;


public interface LinkViewService {

    ResponseMessageOf<LinkResult> fromFileLink(LinkEntity link, Long viewerId);
    ResponseMessageOf<LinkResult> fromDataRoomLink(LinkEntity link, Long viewerId);
    
}
