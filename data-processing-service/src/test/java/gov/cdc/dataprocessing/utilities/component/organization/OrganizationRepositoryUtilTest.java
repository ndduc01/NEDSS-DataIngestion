package gov.cdc.dataprocessing.utilities.component.organization;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.locator.PhysicalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationNameDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.PrepareEntity;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PhysicalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PostalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.TeleLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.Organization;
import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.OrganizationName;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityIdRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.PhysicalLocatorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.PostalLocatorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.TeleLocatorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.organization.OrganizationNameRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.organization.OrganizationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.role.RoleRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.PrepareEntityStoredProcRepository;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrganizationRepositoryUtilTest {
    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private OrganizationNameRepository organizationNameRepository;
    @Mock
    private EntityRepository entityRepository;
    @Mock
    private EntityIdRepository entityIdRepository;
    @Mock
    private EntityLocatorParticipationRepository entityLocatorParticipationRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private TeleLocatorRepository teleLocatorRepository;
    @Mock
    private PostalLocatorRepository postalLocatorRepository;
    @Mock
    private PhysicalLocatorRepository physicalLocatorRepository;
    @Mock
    private IOdseIdGeneratorService odseIdGeneratorService;
    @Mock
    private EntityHelper entityHelper;
    @Mock
    private ParticipationRepository participationRepository;
    @Mock
    private PrepareAssocModelHelper prepareAssocModelHelper;
    @Mock
    private PrepareEntityStoredProcRepository prepareEntityStoredProcRepository;
    @Mock
    AuthUtil authUtil;
    @InjectMocks
    private OrganizationRepositoryUtil organizationRepositoryUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo authUserProfileInfo=new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        authUserProfileInfo.setAuthUser(user);
        authUtil.setGlobalAuthUser(authUserProfileInfo);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(organizationRepository);
        Mockito.reset(organizationNameRepository);
        Mockito.reset(entityRepository);
        Mockito.reset(entityIdRepository);
        Mockito.reset(entityLocatorParticipationRepository);
        Mockito.reset(roleRepository);
        Mockito.reset(teleLocatorRepository);
        Mockito.reset(postalLocatorRepository);
        Mockito.reset(physicalLocatorRepository);
        Mockito.reset(odseIdGeneratorService);
        Mockito.reset(entityHelper);
        Mockito.reset(participationRepository);
        Mockito.reset(prepareAssocModelHelper);
        Mockito.reset(prepareEntityStoredProcRepository);
    }

    @Test
    void findOrganizationByUid() {
        Long orgUid = 123L;
        Organization organization = new Organization();
        organization.setOrganizationUid(orgUid);
        organization.setAddReasonCode("TEST");
        when(organizationRepository.findById(orgUid)).thenReturn(Optional.of(organization));
        Organization organizationResult = organizationRepositoryUtil.findOrganizationByUid(orgUid);
        assertNotNull(organizationResult);
    }

    @Test
    void createOrganization() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        OrganizationDto organizationDto = organizationContainer.getTheOrganizationDto();
        organizationDto.setOrganizationUid(123L);

        OrganizationNameDto orgNameDto = new OrganizationNameDto();
        orgNameDto.setOrganizationUid(123L);
        orgNameDto.setOrganizationNameSeq(1234);
        orgNameDto.setNmTxt("TEST_ORG_NAME");

        Collection<OrganizationNameDto> theOrganizationNameDtoCollection = new ArrayList<>();
        theOrganizationNameDtoCollection.add(orgNameDto);
        organizationContainer.setTheOrganizationNameDtoCollection(theOrganizationNameDtoCollection);

        LocalUidGenerator localIdModel = new LocalUidGenerator();
        localIdModel.setSeedValueNbr(1234L);
        localIdModel.setUidPrefixCd("TEST_PX");
        localIdModel.setUidSuffixCd("TEST_SX");

        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.ORGANIZATION)).thenReturn(localIdModel);
        //Insert Organization
        Organization organization = new Organization(organizationDto);
        when(organizationRepository.save(organization)).thenReturn(organization);
        //Insert OrganizationName
        OrganizationName orgName = new OrganizationName(orgNameDto);
        when(organizationNameRepository.save(orgName)).thenReturn(orgName);

        //EntityID
        EntityIdDto entityIdDto = getEntityIdDto();
        ArrayList<EntityIdDto> entityList = new ArrayList<>();
        entityList.add(entityIdDto);
        organizationContainer.setTheEntityIdDtoCollection(entityList);

        EntityId entityId = new EntityId(entityIdDto);
        when(entityIdRepository.save(entityId)).thenReturn(entityId);

        //Entity Locator Participation
        EntityLocatorParticipationDto entityLocatorParticipationDtoPh = getEntityLocatorParticipationDto_ph();
        EntityLocatorParticipationDto entityLocatorParticipationDtoPo = getEntityLocatorParticipationDto_po();
        EntityLocatorParticipationDto entityLocatorParticipationDtoTel = getEntityLocatorParticipationDto_tel();

        organizationContainer.getTheEntityLocatorParticipationDtoCollection().add(entityLocatorParticipationDtoPh);
        organizationContainer.getTheEntityLocatorParticipationDtoCollection().add(entityLocatorParticipationDtoPo);
        organizationContainer.getTheEntityLocatorParticipationDtoCollection().add(entityLocatorParticipationDtoTel);

        LocalUidGenerator localUidGenerator = getLocalUidGenerator();
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.ORGANIZATION)).thenReturn(localUidGenerator);

        when(physicalLocatorRepository.save(new PhysicalLocator(entityLocatorParticipationDtoPh.getThePhysicalLocatorDto()))).thenReturn((new PhysicalLocator(entityLocatorParticipationDtoPh.getThePhysicalLocatorDto())));
        when(postalLocatorRepository.save(new PostalLocator(entityLocatorParticipationDtoPo.getThePostalLocatorDto()))).thenReturn((new PostalLocator(entityLocatorParticipationDtoPo.getThePostalLocatorDto())));
        when(teleLocatorRepository.save(new TeleLocator(entityLocatorParticipationDtoTel.getTheTeleLocatorDto()))).thenReturn((new TeleLocator(entityLocatorParticipationDtoTel.getTheTeleLocatorDto())));

        //Role
        RoleDto roleDto = getRoleDto();
        Collection<RoleDto> theRoleDTCollection = new ArrayList<>();
        theRoleDTCollection.add(roleDto);
        organizationContainer.setTheRoleDTCollection(theRoleDTCollection);
        when(roleRepository.save(new Role(roleDto))).thenReturn(new Role(roleDto));

        organizationRepositoryUtil.createOrganization(organizationContainer);
        verify(roleRepository, times(1)).save(new Role(roleDto));
    }

    @Test
    void createOrganization_with_null_dependencies() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        OrganizationDto organizationDto = organizationContainer.getTheOrganizationDto();
        organizationDto.setOrganizationUid(123L);

        LocalUidGenerator localIdModel = new LocalUidGenerator();
        localIdModel.setSeedValueNbr(1234L);
        localIdModel.setUidPrefixCd("TEST_PX");
        localIdModel.setUidSuffixCd("TEST_SX");
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.ORGANIZATION)).thenReturn(localIdModel);
        long orgIdResult = organizationRepositoryUtil.createOrganization(organizationContainer);
        assertEquals(1234L, orgIdResult);
    }

    @Test
    void createOrganization_for_throw_exp_on_localId() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        OrganizationDto organizationDto = organizationContainer.getTheOrganizationDto();
        organizationDto.setOrganizationUid(123L);

        OrganizationNameDto orgNameDto = new OrganizationNameDto();
        orgNameDto.setOrganizationUid(123L);
        orgNameDto.setOrganizationNameSeq(1234);
        orgNameDto.setNmTxt("TEST_ORG_NAME");

        Collection<OrganizationNameDto> theOrganizationNameDtoCollection = new ArrayList<>();
        theOrganizationNameDtoCollection.add(orgNameDto);
        organizationContainer.setTheOrganizationNameDtoCollection(theOrganizationNameDtoCollection);

        LocalUidGenerator localIdModel = new LocalUidGenerator();
        localIdModel.setSeedValueNbr(1234L);
        localIdModel.setUidPrefixCd("TEST_PX");
        localIdModel.setUidSuffixCd("TEST_SX");

        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.ORGANIZATION)).thenThrow(Mockito.mock(DataProcessingException.class));
        assertThrows(DataProcessingException.class, () -> organizationRepositoryUtil.createOrganization(organizationContainer));
    }

    @Test
    void updateOrganization() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        OrganizationDto organizationDto = organizationContainer.getTheOrganizationDto();
        organizationDto.setOrganizationUid(123L);

        OrganizationNameDto orgNameDto = new OrganizationNameDto();
        orgNameDto.setOrganizationUid(123L);
        orgNameDto.setNmTxt("TEST_ORG_NAME");

        Collection<OrganizationNameDto> theOrganizationNameDtoCollection = new ArrayList<>();
        theOrganizationNameDtoCollection.add(orgNameDto);
        organizationContainer.setTheOrganizationNameDtoCollection(theOrganizationNameDtoCollection);

        LocalUidGenerator localIdModel = new LocalUidGenerator();
        localIdModel.setSeedValueNbr(1234L);
        localIdModel.setUidPrefixCd("TEST_PX");
        localIdModel.setUidSuffixCd("TEST_SX");

        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.ORGANIZATION)).thenReturn(localIdModel);
        //Insert Organization
        Organization organization = new Organization(organizationDto);
        when(organizationRepository.save(organization)).thenReturn(organization);
        //Insert OrganizationName
        OrganizationName orgName = new OrganizationName(orgNameDto);
        when(organizationNameRepository.save(orgName)).thenReturn(orgName);

        //EntityID
        EntityIdDto entityIdDto = getEntityIdDto();
        ArrayList<EntityIdDto> entityList = new ArrayList<>();
        entityList.add(entityIdDto);
        organizationContainer.setTheEntityIdDtoCollection(entityList);

        EntityId entityId = new EntityId(entityIdDto);
        when(entityIdRepository.save(entityId)).thenReturn(entityId);

        //Entity Locator Participation
        EntityLocatorParticipationDto entityLocatorParticipationDtoPh = getEntityLocatorParticipationDto_ph();
        EntityLocatorParticipationDto entityLocatorParticipationDtoPo = getEntityLocatorParticipationDto_po();
        EntityLocatorParticipationDto entityLocatorParticipationDtoTel = getEntityLocatorParticipationDto_tel();

        organizationContainer.getTheEntityLocatorParticipationDtoCollection().add(entityLocatorParticipationDtoPh);
        organizationContainer.getTheEntityLocatorParticipationDtoCollection().add(entityLocatorParticipationDtoPo);
        organizationContainer.getTheEntityLocatorParticipationDtoCollection().add(entityLocatorParticipationDtoTel);

        LocalUidGenerator localUidGenerator = getLocalUidGenerator();
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.ORGANIZATION)).thenReturn(localUidGenerator);

        when(physicalLocatorRepository.save(new PhysicalLocator(entityLocatorParticipationDtoPh.getThePhysicalLocatorDto()))).thenReturn((new PhysicalLocator(entityLocatorParticipationDtoPh.getThePhysicalLocatorDto())));
        when(postalLocatorRepository.save(new PostalLocator(entityLocatorParticipationDtoPo.getThePostalLocatorDto()))).thenReturn((new PostalLocator(entityLocatorParticipationDtoPo.getThePostalLocatorDto())));
        when(teleLocatorRepository.save(new TeleLocator(entityLocatorParticipationDtoTel.getTheTeleLocatorDto()))).thenReturn((new TeleLocator(entityLocatorParticipationDtoTel.getTheTeleLocatorDto())));

        //Role
        RoleDto roleDto = getRoleDto();
        Collection<RoleDto> theRoleDTCollection = new ArrayList<>();
        theRoleDTCollection.add(roleDto);
        organizationContainer.setTheRoleDTCollection(theRoleDTCollection);
        when(roleRepository.save(new Role(roleDto))).thenReturn(new Role(roleDto));

        organizationRepositoryUtil.updateOrganization(organizationContainer);
        verify(roleRepository, times(1)).save(new Role(roleDto));
    }

    @Test
    void updateOrganization_with_null_dependencies() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        OrganizationDto organizationDto = organizationContainer.getTheOrganizationDto();
        organizationDto.setOrganizationUid(123L);
        organizationRepositoryUtil.updateOrganization(organizationContainer);
        verify(organizationRepository, times(1)).save(any(Organization.class));
    }

    @Test
    void updateOrganization_with_throw_exp() {
        OrganizationContainer organizationContainer = null;
        assertThrows(DataProcessingException.class, () -> organizationRepositoryUtil.updateOrganization(organizationContainer));
    }

    @Test
    void setOrganization() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        OrganizationDto organizationDto = organizationContainer.getTheOrganizationDto();
        organizationDto.setOrganizationUid(123L);
        Long orgIdResult=organizationRepositoryUtil.setOrganization(organizationContainer, "TEST");
        assertEquals(123L,orgIdResult);
    }

    @Test
    void setOrganization_throw_exp() {
        OrganizationContainer organizationContainer = null;
        assertThrows(DataProcessingException.class, () -> organizationRepositoryUtil.setOrganization(organizationContainer, "TEST"));
    }

    @Test
    void setOrganization_for_loadObj() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        OrganizationDto organizationDto = organizationContainer.getTheOrganizationDto();
        organizationDto.setOrganizationUid(123L);
        organizationDto.setVersionCtrlNbr(1);
        organizationContainer.setItNew(false);
        organizationContainer.setItDirty(true);

        Collection<RoleDto> rDTCol = new ArrayList<>();
        Collection<ParticipationDto> pDTCol = new ArrayList<>();
        organizationContainer.setTheRoleDTCollection(rDTCol);
        organizationContainer.setTheParticipationDtoCollection(pDTCol);
        //OrganizationName
        OrganizationNameDto orgNameDto = new OrganizationNameDto();
        orgNameDto.setOrganizationUid(123L);
        orgNameDto.setOrganizationNameSeq(1234);
        orgNameDto.setNmTxt("TEST_ORG_NAME");
        orgNameDto.setNmUseCd("L");

        OrganizationNameDto orgNameDto1 = new OrganizationNameDto();
        orgNameDto1.setOrganizationUid(123L);
        orgNameDto1.setOrganizationNameSeq(1234);
        orgNameDto1.setNmTxt("TEST_ORG_NAME");
        orgNameDto1.setNmUseCd("TEST");

        Collection<OrganizationNameDto> theOrganizationNameDtoCollection = new ArrayList<>();
        theOrganizationNameDtoCollection.add(orgNameDto);
        theOrganizationNameDtoCollection.add(orgNameDto1);
        organizationContainer.setTheOrganizationNameDtoCollection(theOrganizationNameDtoCollection);
        //Select Org
        Organization organization = new Organization();
        organization.setOrganizationUid(123L);
        organization.setAddReasonCode("TEST");

        when(organizationRepository.findById(123L)).thenReturn(Optional.of(organization));
        //Select Org
        List<OrganizationName> organizationNameList = new ArrayList<>();
        OrganizationName orgName = new OrganizationName();
        orgName.setOrganizationUid(123L);
        orgName.setOrganizationNameSeq(1234);
        orgName.setNameText("Test org name");
        organizationNameList.add(orgName);
        when(organizationNameRepository.findByOrganizationUid(123L)).thenReturn(Optional.of(organizationNameList));
        //select EntityId
        List<EntityId> entityIdList = new ArrayList<>();
        EntityId entityId = new EntityId();
        entityId.setEntityUid(123L);
        entityId.setAddReasonCode("Test");
        entityIdList.add(entityId);
        when(entityIdRepository.findByEntityUid(123L)).thenReturn(Optional.of(entityIdList));
        //select EntityLocatorParticipations
        List<EntityLocatorParticipation> entityLocatorParticipations = new ArrayList<>();
        EntityLocatorParticipationDto entityLocatorParticipationDtoPh = getEntityLocatorParticipationDto_ph();
        EntityLocatorParticipationDto entityLocatorParticipationDtoPo = getEntityLocatorParticipationDto_po();
        EntityLocatorParticipationDto entityLocatorParticipationDtoTel = getEntityLocatorParticipationDto_tel();

        EntityLocatorParticipation entityLocatorParticipation_ph = new EntityLocatorParticipation(entityLocatorParticipationDtoPh);
        EntityLocatorParticipation entityLocatorParticipation_po = new EntityLocatorParticipation(entityLocatorParticipationDtoPo);
        EntityLocatorParticipation entityLocatorParticipation_tel = new EntityLocatorParticipation(entityLocatorParticipationDtoTel);
        entityLocatorParticipations.add(entityLocatorParticipation_ph);
        entityLocatorParticipations.add(entityLocatorParticipation_po);
        entityLocatorParticipations.add(entityLocatorParticipation_tel);
        when(entityLocatorParticipationRepository.findByParentUid(123L)).thenReturn(Optional.of(entityLocatorParticipations));
        //
        ArrayList<Participation> participationList = new ArrayList<>();
        Participation participation = new Participation();
        participation.setCode("TEST");
        participation.setSubjectEntityUid(123L);
        participationList.add(participation);

        when(participationRepository.findBySubjectEntityUid(123L)).thenReturn(Optional.of(participationList));
        //select Role
        List<Role> roleList = new ArrayList<>();
        Role roleModel = new Role();
        roleModel.setRoleSeq(1234L);
        roleList.add(roleModel);
        when(roleRepository.findBySubjectEntityUid(Long.valueOf(123L))).thenReturn(Optional.of(roleList));
        //
        OrganizationDto newOrganizationDto = new OrganizationDto();
        newOrganizationDto.setOrganizationUid(1234L);
        newOrganizationDto.setVersionCtrlNbr(2);

        when(prepareAssocModelHelper
                .prepareVO(any(),
                        any(), any(),
                        any(),
                        any(),
                        any()
                )).thenReturn(newOrganizationDto);
        //PrepareEntity
        PrepareEntity prepareEntity = new PrepareEntity();
        prepareEntity.setLocalId("123");
        prepareEntity.setRecordStatusState("TEST_STATUS_CD");
        prepareEntity.setObjectStatusState("TEST_OBJ_STATUS_CD");

        when(prepareEntityStoredProcRepository.getPrepareEntity(any(), any(), any(), any())).thenReturn(prepareEntity);

        Long orgIdResult=organizationRepositoryUtil.setOrganization(organizationContainer, "TEST");
        assertEquals(1234L,orgIdResult);
    }

    @Test
    void setOrganization_for_new_org() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        OrganizationDto organizationDto = organizationContainer.getTheOrganizationDto();
        organizationDto.setOrganizationUid(123L);
        organizationDto.setVersionCtrlNbr(1);
        organizationContainer.setItNew(true);
        organizationContainer.setItDirty(false);

        Collection<RoleDto> rDTCol = new ArrayList<>();
        Collection<ParticipationDto> pDTCol = new ArrayList<>();
        organizationContainer.setTheRoleDTCollection(rDTCol);
        organizationContainer.setTheParticipationDtoCollection(pDTCol);
        //OrganizationName
        OrganizationNameDto orgNameDto = new OrganizationNameDto();
        orgNameDto.setOrganizationUid(123L);
        orgNameDto.setOrganizationNameSeq(1234);
        orgNameDto.setNmTxt("TEST_ORG_NAME");
        orgNameDto.setNmUseCd("L");

        OrganizationNameDto orgNameDto1 = new OrganizationNameDto();
        orgNameDto1.setOrganizationUid(123L);
        orgNameDto1.setOrganizationNameSeq(1234);
        orgNameDto1.setNmTxt("TEST_ORG_NAME");
        orgNameDto1.setNmUseCd("TEST");

        Collection<OrganizationNameDto> theOrganizationNameDtoCollection = new ArrayList<>();
        theOrganizationNameDtoCollection.add(orgNameDto);
        theOrganizationNameDtoCollection.add(orgNameDto1);
        organizationContainer.setTheOrganizationNameDtoCollection(theOrganizationNameDtoCollection);
        //for localid
        LocalUidGenerator localIdModel = new LocalUidGenerator();
        localIdModel.setSeedValueNbr(1234L);
        localIdModel.setUidPrefixCd("TEST_PX");
        localIdModel.setUidSuffixCd("TEST_SX");

        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.ORGANIZATION)).thenReturn(localIdModel);
        //Select Org
        Organization organization = new Organization();
        organization.setOrganizationUid(123L);
        organization.setAddReasonCode("TEST");

        when(organizationRepository.findById(123L)).thenReturn(Optional.of(organization));
        //Select Org
        List<OrganizationName> organizationNameList = new ArrayList<>();
        OrganizationName orgName = new OrganizationName();
        orgName.setOrganizationUid(123L);
        orgName.setOrganizationNameSeq(1234);
        orgName.setNameText("Test org name");
        organizationNameList.add(orgName);
        when(organizationNameRepository.findByOrganizationUid(123L)).thenReturn(Optional.of(organizationNameList));
        //select EntityId
        List<EntityId> entityIdList = new ArrayList<>();
        EntityId entityId = new EntityId();
        entityId.setEntityUid(123L);
        entityId.setAddReasonCode("Test");
        entityIdList.add(entityId);
        when(entityIdRepository.findByEntityUid(123L)).thenReturn(Optional.of(entityIdList));
        //select EntityLocatorParticipations
        List<EntityLocatorParticipation> entityLocatorParticipations = new ArrayList<>();
        EntityLocatorParticipationDto entityLocatorParticipationDtoPh = getEntityLocatorParticipationDto_ph();
        EntityLocatorParticipationDto entityLocatorParticipationDtoPo = getEntityLocatorParticipationDto_po();
        EntityLocatorParticipationDto entityLocatorParticipationDtoTel = getEntityLocatorParticipationDto_tel();

        EntityLocatorParticipation entityLocatorParticipation_ph = new EntityLocatorParticipation(entityLocatorParticipationDtoPh);
        EntityLocatorParticipation entityLocatorParticipation_po = new EntityLocatorParticipation(entityLocatorParticipationDtoPo);
        EntityLocatorParticipation entityLocatorParticipation_tel = new EntityLocatorParticipation(entityLocatorParticipationDtoTel);
        entityLocatorParticipations.add(entityLocatorParticipation_ph);
        entityLocatorParticipations.add(entityLocatorParticipation_po);
        entityLocatorParticipations.add(entityLocatorParticipation_tel);
        when(entityLocatorParticipationRepository.findByParentUid(123L)).thenReturn(Optional.of(entityLocatorParticipations));
        //select Role
        List<Role> roleList = new ArrayList<>();
        Role roleModel = new Role();
        roleModel.setRoleSeq(1234L);
        roleList.add(roleModel);
        when(roleRepository.findBySubjectEntityUid(Long.valueOf(123L))).thenReturn(Optional.of(roleList));
        //
        OrganizationDto newOrganizationDto = new OrganizationDto();
        newOrganizationDto.setOrganizationUid(1234L);
        newOrganizationDto.setVersionCtrlNbr(2);

        when(prepareAssocModelHelper
                .prepareVO(any(),
                        any(), any(),
                        any(),
                        any(),
                        any()
                )).thenReturn(newOrganizationDto);
        //PrepareEntity
        PrepareEntity prepareEntity = new PrepareEntity();
        prepareEntity.setLocalId("123");
        prepareEntity.setRecordStatusState("TEST_STATUS_CD");
        prepareEntity.setObjectStatusState("TEST_OBJ_STATUS_CD");

        when(prepareEntityStoredProcRepository.getPrepareEntity(any(), any(), any(), any())).thenReturn(prepareEntity);

        Long orgIdResult=organizationRepositoryUtil.setOrganization(organizationContainer, "TEST");
        assertEquals(1234L,orgIdResult);
    }

    @Test
    void loadObject() throws DataProcessingException {
        //Select Org
        Organization organization = new Organization();
        organization.setOrganizationUid(123L);
        organization.setAddReasonCode("TEST");

        when(organizationRepository.findById(123L)).thenReturn(Optional.of(organization));
        //Select Org
        List<OrganizationName> organizationNameList = new ArrayList<>();
        OrganizationName orgName = new OrganizationName();
        orgName.setOrganizationUid(123L);
        orgName.setOrganizationNameSeq(1234);
        orgName.setNameText("Test org name");
        organizationNameList.add(orgName);
        when(organizationNameRepository.findByOrganizationUid(123L)).thenReturn(Optional.of(organizationNameList));
        //select EntityId
        List<EntityId> entityIdList = new ArrayList<>();
        EntityId entityId = new EntityId();
        entityId.setEntityUid(123L);
        entityId.setAddReasonCode("Test");
        entityIdList.add(entityId);
        when(entityIdRepository.findByEntityUid(123L)).thenReturn(Optional.of(entityIdList));
        //select EntityLocatorParticipations
        List<EntityLocatorParticipation> entityLocatorParticipations = new ArrayList<>();
        EntityLocatorParticipationDto entityLocatorParticipationDtoPh = getEntityLocatorParticipationDto_ph();
        EntityLocatorParticipationDto entityLocatorParticipationDtoPo = getEntityLocatorParticipationDto_po();
        EntityLocatorParticipationDto entityLocatorParticipationDtoTel = getEntityLocatorParticipationDto_tel();

        EntityLocatorParticipation entityLocatorParticipation_ph = new EntityLocatorParticipation(entityLocatorParticipationDtoPh);
        EntityLocatorParticipation entityLocatorParticipation_po = new EntityLocatorParticipation(entityLocatorParticipationDtoPo);
        EntityLocatorParticipation entityLocatorParticipation_tel = new EntityLocatorParticipation(entityLocatorParticipationDtoTel);
        entityLocatorParticipations.add(entityLocatorParticipation_ph);
        entityLocatorParticipations.add(entityLocatorParticipation_po);
        entityLocatorParticipations.add(entityLocatorParticipation_tel);

        PhysicalLocator physicalLocator = new PhysicalLocator(entityLocatorParticipationDtoPh.getThePhysicalLocatorDto());
        PostalLocator postalLocator = new PostalLocator(entityLocatorParticipationDtoPo.getThePostalLocatorDto());
        TeleLocator teleLocator = new TeleLocator(entityLocatorParticipationDtoTel.getTheTeleLocatorDto());

        List<PhysicalLocator> physicalLocatorList = new ArrayList<>();
        physicalLocatorList.add(physicalLocator);
        List<PostalLocator> postalLocatorList = new ArrayList<>();
        postalLocatorList.add(postalLocator);
        List<TeleLocator> teleLocatorList = new ArrayList<>();
        teleLocatorList.add(teleLocator);

        when(entityLocatorParticipationRepository.findByParentUid(123L)).thenReturn(Optional.of(entityLocatorParticipations));

        when(physicalLocatorRepository.findByPhysicalLocatorUids(any())).thenReturn(Optional.of(physicalLocatorList));
        when(postalLocatorRepository.findByPostalLocatorUids(any())).thenReturn(Optional.of(postalLocatorList));
        when(teleLocatorRepository.findByTeleLocatorUids(any())).thenReturn(Optional.of(teleLocatorList));

        //select Role
        List<Role> roleList = new ArrayList<>();
        Role roleModel = new Role();
        roleModel.setRoleSeq(1234L);
        roleList.add(roleModel);
        when(roleRepository.findBySubjectEntityUid(Long.valueOf(123L))).thenReturn(Optional.of(roleList));
        //
        List<Participation> participationList = new ArrayList<>();
        Participation participation = new Participation();
        participation.setActUid(123L);
        participation.setSubjectEntityUid(123L);
        participation.setCode("TEST");
        participationList.add(participation);

        when(participationRepository.findBySubjectEntityUidAndActUid(123L, 123L)).thenReturn(Optional.of(participationList));

        OrganizationContainer organizationContainer = organizationRepositoryUtil.loadObject(123L, 123L);
        assertNotNull(organizationContainer);
    }

    @Test
    void prepareVO_thow_exp() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setItNew(false);
        organizationDto.setItDirty(false);
        organizationDto.setItDelete(false);
        assertThrows(DataProcessingException.class, () -> organizationRepositoryUtil.prepareVO(organizationDto, "TEST_BTCD", "ORG", "TEST_MODULE"));
    }

    @Test
    void getPrepareEntityForOrganization() throws DataProcessingException {
        when(organizationRepositoryUtil.getPrepareEntityForOrganization("TEST", "TEST", 123L, "TEST")).thenThrow(Mockito.mock(DataProcessingException.class));
        assertThrows(DataProcessingException.class, () -> organizationRepositoryUtil.getPrepareEntityForOrganization("TEST", "TEST", 123L, "TEST"));
    }

    private EntityIdDto getEntityIdDto() {
        EntityIdDto entityIdDto = new EntityIdDto();

        entityIdDto.setEntityUid(123L);
        entityIdDto.setEntityIdSeq(1234);
        entityIdDto.setAddReasonCd("TEST_REASON_CD");
        entityIdDto.setAddTime(new Timestamp(System.currentTimeMillis()));
        entityIdDto.setAddUserId(123L);
        entityIdDto.setAssigningAuthorityCd("TEST");
        entityIdDto.setAssigningAuthorityDescTxt("TEST");
        entityIdDto.setDurationAmt("123");
        entityIdDto.setDurationUnitCd("TEST");
        entityIdDto.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        entityIdDto.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        entityIdDto.setLastChgReasonCd("TEST");
        entityIdDto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        entityIdDto.setLastChgUserId(123L);
        entityIdDto.setRecordStatusCd("TEST");
        entityIdDto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));

        entityIdDto.setRootExtensionTxt("TEST");
        entityIdDto.setStatusCd("TEST");
        entityIdDto.setStatusTime(new Timestamp(System.currentTimeMillis()));
        entityIdDto.setTypeCd("TEST");
        entityIdDto.setTypeDescTxt("TEST");
        entityIdDto.setUserAffiliationTxt("TEST");
        entityIdDto.setValidFromTime(new Timestamp(System.currentTimeMillis()));
        entityIdDto.setValidToTime(new Timestamp(System.currentTimeMillis()));
        entityIdDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        entityIdDto.setAssigningAuthorityIdType("TEST");
        return entityIdDto;
    }

    private EntityLocatorParticipationDto getEntityLocatorParticipationDto_ph() {
        //PHYSICAL
        EntityLocatorParticipationDto entityLocatorDT_ph = new EntityLocatorParticipationDto();
        entityLocatorDT_ph.setClassCd(NEDSSConstant.PHYSICAL);
        entityLocatorDT_ph.setAddUserId(12345L);

        PhysicalLocatorDto physicalLocatorDto = new PhysicalLocatorDto();
        physicalLocatorDto.setLocatorTxt("TEST_TXT");
        physicalLocatorDto.setAddReasonCd("TEST_REASON_CD");
        physicalLocatorDto.setAddUserId(12345L);
        entityLocatorDT_ph.setThePhysicalLocatorDto(physicalLocatorDto);
        return entityLocatorDT_ph;
    }

    private EntityLocatorParticipationDto getEntityLocatorParticipationDto_po() {
        //POSTAL
        EntityLocatorParticipationDto entityLocatorDT_po = new EntityLocatorParticipationDto();
        entityLocatorDT_po.setClassCd(NEDSSConstant.POSTAL);
        entityLocatorDT_po.setAddUserId(12345L);

        PostalLocatorDto thePostalLocatorDto = new PostalLocatorDto();
        thePostalLocatorDto.setAddReasonCd("TEST_REASON_CD");
        thePostalLocatorDto.setAddUserId(12345L);
        entityLocatorDT_po.setThePostalLocatorDto(thePostalLocatorDto);
        return entityLocatorDT_po;
    }

    private EntityLocatorParticipationDto getEntityLocatorParticipationDto_tel() {
        //TELE
        EntityLocatorParticipationDto entityLocatorDT_tel = new EntityLocatorParticipationDto();
        entityLocatorDT_tel.setClassCd(NEDSSConstant.TELE);
        entityLocatorDT_tel.setAddUserId(12345L);

        TeleLocatorDto teleLocatorDto = new TeleLocatorDto();
        teleLocatorDto.setAddReasonCd("TEST_REASON_CD");
        teleLocatorDto.setAddUserId(12345L);
        entityLocatorDT_tel.setTheTeleLocatorDto(teleLocatorDto);
        return entityLocatorDT_tel;
    }

    private LocalUidGenerator getLocalUidGenerator() {
        LocalUidGenerator newLocalId = new LocalUidGenerator();
        newLocalId.setUidSuffixCd("TEST_SFCD");
        newLocalId.setUidPrefixCd("TEST_SFCD");
        newLocalId.setTypeCd("TYPE_CD");
        newLocalId.setClassNameCd("TEST_CL_CD");
        newLocalId.setSeedValueNbr(123456L);
        return newLocalId;
    }

    private RoleDto getRoleDto() {
        RoleDto roleDto = new RoleDto();
        roleDto.setRoleSeq(12345L);
        return roleDto;
    }
}