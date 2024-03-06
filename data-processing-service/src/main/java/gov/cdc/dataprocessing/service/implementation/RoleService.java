package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.role.RoleRepository;
import gov.cdc.dataprocessing.service.interfaces.IRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class RoleService implements IRoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Collection<RoleDto> findRoleScopedToPatient(Long uid) {
        Collection<RoleDto> roleDtoCollection = new ArrayList<>();
        var result = roleRepository.findRoleScopedToPatient(uid);
        if (result.isPresent()) {
            for(var item: result.get()) {
                var elem = new RoleDto(item);
                elem.setItNew(false);
                elem.setItDirty(false);

                roleDtoCollection.add(elem);
            }
        }

        return roleDtoCollection;
    }
}
