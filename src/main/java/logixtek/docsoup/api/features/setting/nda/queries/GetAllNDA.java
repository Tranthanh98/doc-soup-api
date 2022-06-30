package logixtek.docsoup.api.features.setting.nda.queries;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.entities.FileEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;


public class GetAllNDA extends BaseIdentityCommand<ResponseEntity<List<FileEntity>>> {

}

