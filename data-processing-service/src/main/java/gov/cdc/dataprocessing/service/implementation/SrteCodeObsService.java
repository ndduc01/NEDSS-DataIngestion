package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ObsValueCodedDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ObservationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.ObservationVO;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.ProgAreaSnomeCodeStoredProcRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.*;
import gov.cdc.dataprocessing.service.interfaces.ISrteCodeObsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SrteCodeObsService implements ISrteCodeObsService {
    private boolean programAreaDerivationExcludeFlag = false;
    private static final Logger logger = LoggerFactory.getLogger(SrteCodeObsService.class);

    private final ProgAreaSnomeCodeStoredProcRepository progAreaSnomeCodeStoredProcRepository;
    private final SnomedConditionRepository snomedConditionRepository;
    private final LOINCCodeRepository loincCodeRepository;
    private final LabResultRepository labResultRepository;
    private final LabTestRepository labTestRepository;
    private final LabTestLoincRepository labTestLoincRepository;
    private final LabResultSnomedRepository labResultSnomedRepository;
    private final SnomedCodeRepository snomedCodeRepository;
    private final ConditionCodeRepository conditionCodeRepository;


    public SrteCodeObsService(ProgAreaSnomeCodeStoredProcRepository progAreaSnomeCodeStoredProcRepository,
                              SnomedConditionRepository snomedConditionRepository,
                              LOINCCodeRepository loincCodeRepository,
                              LabResultRepository labResultRepository,
                              LabTestRepository labTestRepository,
                              LabTestLoincRepository labTestLoincRepository,
                              LabResultSnomedRepository labResultSnomedRepository,
                              SnomedCodeRepository snomedCodeRepository,
                              ConditionCodeRepository conditionCodeRepository) {
        this.progAreaSnomeCodeStoredProcRepository = progAreaSnomeCodeStoredProcRepository;
        this.snomedConditionRepository = snomedConditionRepository;
        this.loincCodeRepository = loincCodeRepository;
        this.labResultRepository = labResultRepository;
        this.labTestRepository = labTestRepository;
        this.labTestLoincRepository = labTestLoincRepository;
        this.labResultSnomedRepository = labResultSnomedRepository;
        this.snomedCodeRepository = snomedCodeRepository;
        this.conditionCodeRepository = conditionCodeRepository;
    }

    public Map<String, Object> getSnomed(String code, String type, String clia) throws DataProcessingException {
        return progAreaSnomeCodeStoredProcRepository.getSnomed(code, type, clia);
    }

    public String getConditionForSnomedCode(String snomedCd) {
        var result = snomedConditionRepository.getConditionForSnomedCode(snomedCd);

        if (result.isPresent()) {
            return result.get().get(0);
        }
        return "";
    }

    public String getConditionForLoincCode(String loinCd) {
        var result = loincCodeRepository.findConditionForLoincCode(loinCd);
        if(result.isPresent()) {
            return result.get().get(0);
        }
        else {
            return "";
        }
    }

    public String getDefaultConditionForLocalResultCode(String labResultCd, String laboratoryId) {
        var result = labResultRepository.findDefaultConditionCdByLabResultCdAndLaboratoryId(labResultCd, laboratoryId);
        if (result.isPresent()) {
            return result.get().get(0);
        }
        else {
            return "";
        }
    }

    public String getDefaultConditionForLabTest(String labTestCd, String laboratoryId) {
        var result = labTestRepository.findDefaultConditionForLabTest(labTestCd, laboratoryId);
        if (result.isPresent()) {
            return result.get().get(0);
        }
        else {
            return "";
        }
    }

    public ObservationVO labLoincSnomedLookup(ObservationVO obsVO, String labClia) {
        /*
         * Ajith 7/15/05
         * When a reporting facility without a clia number is selected by the user, the droplist
         * of lab tests presented comes from the "DEFAULT" group. Furthermore, even "DEFAULT" lab tests
         * and results can be mapped to LOINC and SNOMEDs( respectively ) just as the lab specific tests
         * and results using the labtest_loinc and labresult_snomed DWYER tables.
         * Because of these reasons, we must use the clia number "DEFAULT" when no clia is available
         * for the selected facility. Otherwise alternate codes coming out of LOINC and SNOMED associations
         * will not be set on the OT and RT.
         *
         */
        if (labClia == null && obsVO.getTheObservationDT().getCdSystemCd().equals(NEDSSConstant.DEFAULT))
        {
            labClia = NEDSSConstant.DEFAULT ;
        }
        if (labClia == null )
        {
            return obsVO;
        }
        doLoincCdLookupForObservationDT(obsVO.getTheObservationDT(), labClia);
        doSnomedCdLookupForObsValueCodedDTs(obsVO.getTheObsValueCodedDTCollection(), labClia);
        return obsVO;
    }

    private void doLoincCdLookupForObservationDT(ObservationDT obsDT, String labClia) {
        String cdSystemCd = obsDT.getCdSystemCd();
        String altCdSystemCd = obsDT.getAltCdSystemCd();

        if (cdSystemCd != null && !cdSystemCd.equals("LN") && altCdSystemCd == null) {
            var result = labTestLoincRepository.findLoincCds(labClia, obsDT.getCd());
            List<String> loincCdList = new ArrayList<>();
            if (result.isPresent()) {
                loincCdList = result.get();
                if (loincCdList != null && loincCdList.size() == 1) {
                    obsDT.setAltCdSystemCd("LN");
                    obsDT.setAltCd( (String) loincCdList.get(0));
                    obsDT.setCdDerivedInd("Y");
                }
            }

        }
    }

    private void doSnomedCdLookupForObsValueCodedDTs(Collection<ObsValueCodedDT> obsValueCodedDTs, String labClia) {
        if (obsValueCodedDTs == null || obsValueCodedDTs.isEmpty())
        {
            return;
        }
        for (Iterator<ObsValueCodedDT> it = obsValueCodedDTs.iterator(); it.hasNext(); ) {
            ObsValueCodedDT obsValueCodedDT = (ObsValueCodedDT) it.next();
            if (obsValueCodedDT == null)
            {
                continue;
            }
            String cdSystemCd = obsValueCodedDT.getCodeSystemCd();
            String altCdSystemCd = obsValueCodedDT.getAltCdSystemCd();
            if (cdSystemCd != null && !cdSystemCd.equals("SNM") && altCdSystemCd == null) {
                var result = labResultSnomedRepository.findSnomedCds(labClia, obsValueCodedDT.getCode());;
                List<String> snomedCdList = new ArrayList<>();
                if (result.isPresent()) {
                    snomedCdList = result.get();
                    //If only one snomed cd found, use it, otherwise discard
                    if (snomedCdList != null && snomedCdList.size() == 1) {
                        obsValueCodedDT.setAltCdSystemCd("SNM");
                        obsValueCodedDT.setAltCd( (String) snomedCdList.get(0));
                        obsValueCodedDT.setCodeDerivedInd("Y");
                    }
                }
            }

        }
    }


    public HashMap<Object, Object> getProgramArea(String reportingLabCLIA, Collection<ObservationVO> observationVOCollection, String electronicInd) throws DataProcessingException {
        HashMap<Object, Object> returnMap = new HashMap<Object, Object>();
        if (reportingLabCLIA == null)
        {
            returnMap.put(NEDSSConstant.ERROR, NEDSSConstant.REPORTING_LAB_CLIA_NULL);
            return returnMap;
        }

        Iterator<ObservationVO> obsIt = observationVOCollection.iterator();
        Hashtable<Object, Object> paHTBL = new Hashtable<Object, Object>();

        //iterator through each resultTest
        while (obsIt.hasNext())
        {
            ObservationVO obsVO = (ObservationVO) obsIt.next();
            ObservationDT obsDt = obsVO.getTheObservationDT();

            String obsDomainCdSt1 = obsDt.getObsDomainCdSt1();
            String obsDTCode = obsDt.getCd();
            boolean found = false;

            //Set exclude flag to false - if any of the components - Lab Result (SNOMED or Local) or Lab Test (LOINC or
            //Local) is excluded, this flag will be set so as not to fail the derivation for this resulted test.
            programAreaDerivationExcludeFlag = false;

            // make sure you are dealing with a resulted test here.
            if ( (obsDomainCdSt1 != null)
                    && obsDomainCdSt1.equals(ELRConstant.ELR_OBSERVATION_RESULT)
                    && (obsDTCode != null)
                    && (!obsDTCode.equals(NEDSSConstant.ACT114_TYP_CD))
            )
            {
                // Retrieve PAs using Lab Result --> SNOMED code mapping
                // If ELR, use actual CLIA - if manual use "DEFAULT" as CLIA
                String progAreaCd;
                if ( electronicInd.equals(NEDSSConstant.ELECTRONIC_IND_ELR) )
                {
                    progAreaCd = getPAFromSNOMEDCodes(reportingLabCLIA, obsVO.getTheObsValueCodedDTCollection());
                }
                else
                {
                    progAreaCd = getPAFromSNOMEDCodes(NEDSSConstant.DEFAULT, obsVO.getTheObsValueCodedDTCollection());
                }


                // If PA returned, check to see if it is the same one as before.
                if (progAreaCd != null)
                {
                    found = true;
                    paHTBL.put(progAreaCd.trim(), progAreaCd.trim());
                    if (paHTBL.size() != 1)
                    {
                        break;
                    }

                }

                // Retrieve PAs using Resulted Test --> LOINC mapping
                if (!found)
                {
                    progAreaCd = getPAFromLOINCCode(reportingLabCLIA, obsVO);
                    // If PA returned, check to see if it is the same one as before.
                    if (progAreaCd != null)
                    {
                        found = true;
                        paHTBL.put(progAreaCd.trim(), progAreaCd.trim());
                        if (paHTBL.size() != 1)
                        {
                            break;
                        }
                    }
                }

                // Retrieve PAs using Local Result Code to PA mapping
                if (!found)
                {
                    progAreaCd = getPAFromLocalResultCode(reportingLabCLIA, obsVO.getTheObsValueCodedDTCollection());
                    // If PA returned, check to see if it is the same one as before.
                    if (progAreaCd != null)
                    {
                        found = true;
                        //System.out.println("Found!" + progAreaCd);
                        paHTBL.put(progAreaCd.trim(), progAreaCd.trim());
                        if (paHTBL.size() != 1)
                        {
                            break;
                        }
                    }
                }

                // Retrieve PAs using Local Result Code to PA mapping
                if (!found)
                {
                    progAreaCd = getPAFromLocalTestCode(reportingLabCLIA, obsVO);
                    // If PA returned, check to see if it is the same one as before.
                    if (progAreaCd != null)
                    {
                        found = true;
                        paHTBL.put(progAreaCd.trim(), progAreaCd.trim());
                        if (paHTBL.size() != 1)
                        {
                            break;
                        }
                    }
                }

                //If we haven't found a PA and the no components were excluded based on the exclude flag,
                //clear the PA hashtable which will fail the derivation
                if (!found && !programAreaDerivationExcludeFlag)
                {
                    paHTBL.clear();
                    break;
                }
            }
        } //end of while

        if(paHTBL.size() == 0)
        {
            returnMap.put(NEDSSConstant.ERROR, ELRConstant.PROGRAM_ASSIGN_2);
        }
        else if (paHTBL.size() == 1)
        {
            returnMap.put(ELRConstant.PROGRAM_AREA_HASHMAP_KEY, paHTBL.keys().nextElement().toString());
        }
        else
        {
            returnMap.put(NEDSSConstant.ERROR, ELRConstant.PROGRAM_ASSIGN_1);
        }
        return returnMap;
    } //end of getProgramArea


    /**
     * Returns a collection of Snomed codes to be used to resolve the program area code.
     * If more than one type of snomed is resolved, return null.
     * @param reportingLabCLIA : String
     * @return Vector
     */
    // AK - 7/25/04
    private String getPAFromSNOMEDCodes(String reportingLabCLIA, Collection<ObsValueCodedDT> obsValueCodedDtColl) throws DataProcessingException {
        Vector<Object> snomedVector = new Vector<Object>();
        if (reportingLabCLIA == null)
        {
            return null;
        }

        if (obsValueCodedDtColl != null) {
            Iterator<ObsValueCodedDT> codedDtIt = obsValueCodedDtColl.iterator();
            while (codedDtIt.hasNext()) {

                ObsValueCodedDT codedDt = (ObsValueCodedDT) codedDtIt.next();
                String codeSystemCd = codedDt.getCodeSystemCd();

                if (codeSystemCd == null || codeSystemCd.trim().equals(""))
                {
                    continue;
                }

                String obsCode = codedDt.getCode();
                if (obsCode == null || obsCode.trim().equals(""))
                {
                    continue;
                }

                /*
                 * 	Check if ObsValueCodedDT.codeSystemCd='L' and CLIA for Reporting Lab is available,
                 *  find Snomed For ObsValueCodedDT.code(Local Result to Snomed lookup)
                 */
                if (!codeSystemCd.equals(ELRConstant.ELR_SNOMED_CD)){
                    // If local code and it is not excluded from PA Derivation, attempt to retrieve corresponding SNOMED code
                    if (!removePADerivationExcludedLabResultCodes(obsCode, reportingLabCLIA)) {
                        Map<String, Object> snomedList =  getSnomed(codedDt.getCode(), ELRConstant.TYPE, reportingLabCLIA);
                        if(snomedList.containsKey("COUNT") && (Integer) snomedList.get("COUNT") == 1) {
                            snomedVector.addElement(snomedList.get("LOINC"));
                        }
                    } else {
                        //If so, set exclude flag so we won't fail this resulted test if no PA is derived for it
                        programAreaDerivationExcludeFlag = true;
                    }
                }

                /*  If already coded using SNOMED code, just add it to the return array.
                 *  check if ObsValueCodedDT.codeSystemCd="SNM", use ObsValueCodedDT.code for Snomed
                 *  Need to check SNOMED codes for Program Area Derivation Exclusion flag - don't include codes with this set
                 */
                else if (codeSystemCd.equals("SNM")) {
                    // If snomed code and it is not excluded from PA Derivation, add it to the SNOMED Vector
                    if (!removePADerivationExcludedSnomedCodes(obsCode))
                    {
                        snomedVector.addElement(obsCode);
                    }
                    else
                    {
                        //Otherwise don't add it and set the exclude flag so we won't fail this resulted test if no PA is derived for it
                        programAreaDerivationExcludeFlag = true;
                    }
                }
            } //end of while


            // Now that we have resolved all the SNOMED codes, try to derive the PA
            if (snomedVector != null && snomedVector.size() == obsValueCodedDtColl.size())
            {
                return getProgAreaCd(snomedVector, reportingLabCLIA, "NEXT", ELRConstant.ELR_SNOMED_CD);
            }

        } //end of if

        return null;
    }

    private boolean removePADerivationExcludedSnomedCodes(String snomedCd) {
        var result = snomedCodeRepository.findSnomedProgramAreaExclusion(snomedCd);
        if (result.isPresent()) {
            for(var item: result.get()) {
                if (item.getPaDerivationExcludeCd() != null && item.getPaDerivationExcludeCd().equals(NEDSSConstant.YES))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        return false;

    }

    private boolean removePADerivationExcludedLabResultCodes(String labResultCd, String reportingLabCLIA) {
        var result = labResultRepository.findLabResultProgramAreaExclusion(labResultCd, reportingLabCLIA);
        if (result.isPresent()) {
            for(var item : result.get()) {
                var elem = item;
                if (elem.getPaDerivationExcludeCd() != null && elem.getPaDerivationExcludeCd().equals(NEDSSConstant.YES))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        return false;
    }


    /**
     * Goes to the database to obtain the Program Area Codes based on the specified parameters.
     * @param codeVector : Vector
     * @param reportingLabCLIA : String
     * @param nextLookUp : String
     * @param type : String - LN for Loinc, SNM for Snomed, LT for Local Test and LR for local result.
     * @return String
     *
     */
    // AK 7/25/04
    private String getProgAreaCd(Vector<Object> codeVector, String reportingLabCLIA, String nextLookUp, String type) {
        if (codeVector == null || codeVector.size() == 0)
        {
            return null;
        }

        Map<String, Object> progAreaCdList = null;
        Vector<Object> toReturn = new Vector<Object>();
        String lastPACode = null;

        try {
            for (int k = 0; k < codeVector.size(); k++) {
                progAreaCdList = progAreaSnomeCodeStoredProcRepository.getProgAreaCd( (String) codeVector.elementAt(k), type, reportingLabCLIA);

                // The above method returns the count of PAs found at
                // index 1 and program area at index 0
                // Return null if we got more than one PA
                if (!progAreaCdList.containsKey("COUNT")) {
                    return null;
                }
                String currentPAcode = (String) progAreaCdList.get("PROGRAM");

                // Compare with previously retrieved PA and return null if they are different.
                if (lastPACode == null)
                {
                    lastPACode = currentPAcode;
                }
                else if (!currentPAcode.equals(lastPACode))
                {
                    return null;
                }
            } //end of for
        }
        catch (Exception e) {
            return null; //break out
        } //end of catch
        return lastPACode;
    } //end of getProgAreaCd()


    /**
     * Attempts to resolve a ProgramAreaCd based on Loinc.
     * @param reportingLabCLIA : String
     * @return loincVector : Vector
     */
    // AK - 7/25/04
    private String getPAFromLOINCCode(String reportingLabCLIA, ObservationVO resultTestVO) throws DataProcessingException {

        ObservationDT obsDt = resultTestVO.getTheObservationDT();
        if (obsDt == null || reportingLabCLIA == null)
        {
            return null;
        }

        String cdSystemCd = obsDt.getCdSystemCd();
        if (cdSystemCd == null || cdSystemCd.trim().equals(""))
        {
            return null;
        }

        String obsCode = obsDt.getCd();
        if (obsCode == null || obsCode.trim().equals(""))
        {
            return null;
        }

        Vector<Object> loincVector = new Vector<Object>();

        if(cdSystemCd.equals(ELRConstant.ELR_OBSERVATION_LOINC))
        {
            //Check if this loinc code should be excluded from Program Area derivation
            //If so, set exclude flag so we won't fail this resulted test if no PA is derived for it
            if (removePADerivationExcludedLoincCodes(obsCode)){
                programAreaDerivationExcludeFlag = true;
                return null;
            }

            loincVector.addElement(obsCode);
        }
        else
        {
            //Check if this local test code should be excluded from Program Area derivation
            //If so, set exclude flag so we won't fail this resulted test if no PA is derived for it
            if (removePADerivationExcludedLabTestCodes(obsCode, reportingLabCLIA)) {
                programAreaDerivationExcludeFlag = true;
                return null;
            }

            Map<String, Object> loincList =  progAreaSnomeCodeStoredProcRepository.getSnomed(obsCode, "LT", reportingLabCLIA);
            if ( loincList.containsKey ("COUNT") && (Integer) loincList.get("COUNT") == 1)
            {
                loincVector.addElement(loincList.get("LOINC"));
            }
        }

        // Now that we have resolved all the LOINC codes, try to derive the PA
        return getProgAreaCd(loincVector, reportingLabCLIA, "NEXT", ELRConstant.ELR_OBSERVATION_LOINC);
    } //end of getLoincColl(...)

    private boolean removePADerivationExcludedLabTestCodes(String labTestCd, String reportingLabCLIA) {
        var result =  labTestRepository.findLabTestForExclusion(labTestCd, reportingLabCLIA);
        if(result.isPresent()) {
            for(var item : result.get()) {
                if (item.getPaDerivationExcludeCd() != null && item.getPaDerivationExcludeCd().equals(NEDSSConstant.YES))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        return false;
    }

    private boolean removePADerivationExcludedLoincCodes(String loincCd) {
        var result =  loincCodeRepository.findLoinCCodeExclusion(loincCd);
        if(result.isPresent()) {
            for(var item : result.get()) {
                if (item.getPaDerivationExcludeCode() != null && item.getPaDerivationExcludeCode().equals(NEDSSConstant.YES))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Attempts to resolve a program area cd based on Local Result code.
     * @param reportingLabCLIA : String
     * @return progrAreaCd : String
     */
    // AK - 7/25/04
    private String getPAFromLocalResultCode(String reportingLabCLIA, Collection<ObsValueCodedDT> obsValueCodedDtColl) {
        String lastProgAreaCd = null;
        String progAreaCd = null;

        if (obsValueCodedDtColl == null || reportingLabCLIA == null)
        {
            return null;
        }

        Vector<Object> codeVector = new Vector<Object>();
        Iterator<ObsValueCodedDT> codedDtIt = obsValueCodedDtColl.iterator();

        while (codedDtIt.hasNext()) {
            ObsValueCodedDT obsValueCodedDT = (ObsValueCodedDT) codedDtIt.next();
            String code = obsValueCodedDT.getCode();
            String codeSystemCd = obsValueCodedDT.getCodeSystemCd();
            if (code != null && codeSystemCd!=null && !codeSystemCd.equals(ELRConstant.ELR_SNOMED_CD)) {
                //Check if this local result code should be excluded from Program Area derivation
                if (!removePADerivationExcludedLabResultCodes(code, reportingLabCLIA)){
                    codeVector.addElement(code);
                } else {
                    //If so, set exclude flag so we won't fail this resulted test if no PA is derived for it
                    programAreaDerivationExcludeFlag = true;
                }

            }
            progAreaCd = findLocalResultDefaultConditionProgramAreaCd(codeVector, reportingLabCLIA, "NEXT");

            if (progAreaCd == null) {
                progAreaCd = findLocalResultDefaultConditionProgramAreaCdFromLabResult(codeVector, reportingLabCLIA, "NEXT");
            }

            if (lastProgAreaCd == null)
            {
                lastProgAreaCd = progAreaCd;
            }
            else {
                if (!lastProgAreaCd.equals(progAreaCd))
                {
                    return null;
                }
            }
        }
        return lastProgAreaCd;

    } //end of method

    private String findLocalResultDefaultConditionProgramAreaCd(Vector<Object> codeVector, String reportingLabCLIA, String nextLookup) {
        Vector<Object> toReturn = new Vector<Object>();
        String lastPACode = null;
        try {
            for (int k = 0; k < codeVector.size(); k++) {
                var result = conditionCodeRepository.findConditionCodeByLabResultLabIdAndCd(
                        codeVector.elementAt(k).toString(),
                        reportingLabCLIA);
                Collection<String> defaultPACColl = result.get();
                if (defaultPACColl.size() == 1) {
                    String currentPACode = defaultPACColl.iterator().next().toString();
                    // Compare with previously retrieved PA and return null if they are different.
                    if (lastPACode == null)
                    {
                        lastPACode = currentPACode;
                    }
                    else if (!currentPACode.equals(lastPACode))
                    {
                        return null;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null; //????leave observation.progAreaCd == null?????
        } //end of catch
        return lastPACode;
    } //end of getProgAreaCdLocalDefault(...)

    private String findLocalResultDefaultConditionProgramAreaCdFromLabResult(Vector<Object> codeVector, String reportingLabCLIA, String nextLookup) {
        Vector<Object> toReturn = new Vector<Object>();
        String lastPACode = null;
        try {
            for (int k = 0; k < codeVector.size(); k++) {
                var result = labResultRepository.findLocalResultDefaultProgramAreaCd(
                        codeVector.elementAt(k).toString(),
                        reportingLabCLIA);
                Collection<String> defaultPACColl = result.get();
                if (defaultPACColl.size() == 1) {
                    String currentPACode = defaultPACColl.iterator().next().toString();
                    // Compare with previously retrieved PA and return null if they are different.
                    if (lastPACode == null)
                    {
                        lastPACode = currentPACode;
                    }
                    else if (!currentPACode.equals(lastPACode))
                    {
                        return null;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null; //????leave observation.progAreaCd == null?????
        } //end of catch
        return lastPACode;
    } //end of getProgAreaCdLocalDefault(...)


    private String findLocalResultDefaultConditionProgramAreaCdFromLabTest(Vector<Object> codeVector, String reportingLabCLIA, String nextLookup) {
        Vector<Object> toReturn = new Vector<Object>();
        String lastPACode = null;
        try {
            for (int k = 0; k < codeVector.size(); k++) {
                var result = labTestRepository.findLocalTestDefaultConditionProgramAreaCd(
                        codeVector.elementAt(k).toString(),
                        reportingLabCLIA);
                Collection<String> defaultPACColl = result.get();
                if (defaultPACColl.size() == 1) {
                    String currentPACode = defaultPACColl.iterator().next().toString();
                    // Compare with previously retrieved PA and return null if they are different.
                    if (lastPACode == null)
                    {
                        lastPACode = currentPACode;
                    }
                    else if (!currentPACode.equals(lastPACode))
                    {
                        return null;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null; //????leave observation.progAreaCd == null?????
        } //end of catch
        return lastPACode;
    } //end of getProgAreaCdLocalDefault(...)

    private String findLocalResultDefaultConditionProgramAreaCdFromLabTestWithoutJoin(Vector<Object> codeVector, String reportingLabCLIA, String nextLookup) {
        Vector<Object> toReturn = new Vector<Object>();
        String lastPACode = null;
        try {
            for (int k = 0; k < codeVector.size(); k++) {
                var result = labTestRepository.findLocalTestDefaultProgramAreaCd(
                        codeVector.elementAt(k).toString(),
                        reportingLabCLIA);
                Collection<String> defaultPACColl = result.get();
                if (defaultPACColl.size() == 1) {
                    String currentPACode = defaultPACColl.iterator().next().toString();
                    // Compare with previously retrieved PA and return null if they are different.
                    if (lastPACode == null)
                    {
                        lastPACode = currentPACode;
                    }
                    else if (!currentPACode.equals(lastPACode))
                    {
                        return null;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null; //????leave observation.progAreaCd == null?????
        } //end of catch
        return lastPACode;
    } //end of getProgAreaCdLocalDefault(...)



    /**
     * Attempts to resolve a program area cd based on LocalTestDefault cd.
     * @param reportingLabCLIA : String
     * @return progAreaCd : String
     */
    // AK - 7/25/04
    private String getPAFromLocalTestCode(String reportingLabCLIA,
                                         ObservationVO resultTestVO) {

        ObservationDT obsDt = resultTestVO.getTheObservationDT();

        //If this test has a LOINC, we should return and not treat it as a local test 
        if (obsDt.equals(ELRConstant.ELR_OBSERVATION_LOINC))
        {
            return null;
        }

        String code = getLocalTestCode(obsDt);

        if (reportingLabCLIA == null || code == null || code.trim().equals(""))
        {
            return null;
        }

        //Check if this code should be excluded from Program Area derivation
        if (removePADerivationExcludedLabTestCodes(code, reportingLabCLIA))
        {
            return null;
        }

        String progAreaCd = null;

        Vector<Object> codeVector = new Vector<Object>();
        codeVector.addElement(code);

        String codeSql = null;

        progAreaCd = findLocalResultDefaultConditionProgramAreaCdFromLabTest(codeVector, reportingLabCLIA, "NEXT");

        if (progAreaCd == null) {
            progAreaCd = findLocalResultDefaultConditionProgramAreaCdFromLabTestWithoutJoin(codeVector, reportingLabCLIA, "NEXT");
        }
        return progAreaCd;

    } //end of method

    /**
     * Returns the code that will be used to help resolve the program area cd
     * @param obsDt : ObservationDT
     * @return code : String
     */
    private String getLocalTestCode(ObservationDT obsDt)
    {
        String code = null;
        if (obsDt != null)
        {
            if (obsDt.getCdSystemCd() != null)
            {
                if (obsDt.getCd() != null && !obsDt.getCd().equals("") &&
                        !obsDt.getCd().equals(" "))
                {
                    code = obsDt.getCd();
                }
            } //end of if
        } //end of if
        return code;
    } //end of getLocalTestColl
}
