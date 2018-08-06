/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.job.common.comparators.statuscodes;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.communication.api.metadata.ComparatorStepResult.Status;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.ArtifactDAOMock;
import com.cognifide.aet.job.common.comparators.AbstractComparatorTest;
import java.util.ArrayList;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * This Unit test is responsible for validating more of component logic then actual filtering. Tests
 * for filter are written separately and can be found in StatusCodesFilterTest
 */
@RunWith(MockitoJUnitRunner.class)
public class StatusCodesComparatorTest extends AbstractComparatorTest {

  private static final String PARAM_FILTER_RANGE = "filterRange";

  private static final String PARAM_FILTER_CODES = "filterCodes";

  private static final String FILTER_RANGE_UPPER_LESS_THAN_LOWER = "500,300";

  private static final String FILTER_RANGE_NAN = "not,a_number";

  private static final String FILTER_RANGE_INVALID = "cannot split";

  private static final String FILTER_RANGE = "300,500";

  private static final String FILTER_CODES_SINGLE = "302";

  private static final String FILTER_CODES_MULTIPLE = "302,303,404";

  private static final String FILTER_CODES_SINGLE_NAN = "not-a-number";

  private static final String FILTER_CODES_MULTIPLE_NAN = "NaN,NaN,NaN,NaN";

  private static final String FILTER_CODES_ONLY_SUCCESS = "200";

  private static final String FILTER_RANGE_REMOVE_DEFAULT_RANGE = "1,1";

  private static final String FILTER_CODES_EXCLUDED = "500";

  private StatusCodesComparator tested;

  @Mock
  private Map<String, String> params;

  @Mock
  private ComparatorProperties comparatorProperties;

  @Before
  public void setUp() {
    artifactDaoMock = new ArtifactDAOMock(StatusCodesComparator.class);
    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(false);
    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(false);
  }

  @Test
  public void compareTest_expectPassed() throws ProcessingException {
    tested = createNewStatusCodesComparator("expected-data-200-result.json");

    result = tested.compare();
    assertEquals(ComparatorStepResult.Status.PASSED, result.getStatus());
  }

  @Test
  public void compareTest_expectFailed() throws ProcessingException, ParametersException {
    tested = createNewStatusCodesComparator("expected-data-200-result.json");

    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(true);
    when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_ONLY_SUCCESS);
    tested.setParameters(params);

    result = tested.compare();
    assertEquals(ComparatorStepResult.Status.FAILED, result.getStatus());
  }

  @Test
  public void compareTest_filterRange_expectPassed()
      throws ProcessingException, ParametersException {
    tested = createNewStatusCodesComparator("not-existing-page-404-result.json");

    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE_REMOVE_DEFAULT_RANGE);
    tested.setParameters(params);

    result = tested.compare();
    assertEquals(ComparatorStepResult.Status.PASSED, result.getStatus());
  }

  @Test
  public void compareTest_filterRange_expectFailed()
      throws ProcessingException, ParametersException {
    tested = createNewStatusCodesComparator("not-existing-page-404-result.json");

    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE);
    tested.setParameters(params);

    result = tested.compare();
    assertEquals(ComparatorStepResult.Status.FAILED, result.getStatus());
  }

  @Test
  public void compareTest_filterCodes_expectPassed()
      throws ProcessingException, ParametersException {
    tested = createNewStatusCodesComparator("not-existing-page-404-result.json");

    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(true);
    when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_ONLY_SUCCESS);
    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE_REMOVE_DEFAULT_RANGE);
    tested.setParameters(params);

    result = tested.compare();
    assertEquals(ComparatorStepResult.Status.PASSED, result.getStatus());
  }

  @Test
  public void compareTest_filterCodes_expectFailed()
      throws ProcessingException, ParametersException {
    tested = createNewStatusCodesComparator("not-existing-page-404-result.json");

    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(true);
    when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_MULTIPLE);
    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE_REMOVE_DEFAULT_RANGE);
    tested.setParameters(params);

    result = tested.compare();
    assertEquals(ComparatorStepResult.Status.FAILED, result.getStatus());
  }

  @Test
  public void compareTest_filterCodesMultipleCodes_expectFailed()
      throws ProcessingException, ParametersException {
    tested = createNewStatusCodesComparator("not-existing-page-404-result.json");

    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(true);
    when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_MULTIPLE);
    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE_REMOVE_DEFAULT_RANGE);
    tested.setParameters(params);

    result = tested.compare();
    assertEquals(ComparatorStepResult.Status.FAILED, result.getStatus());
  }

  @Test
  public void compareTest_filterCodesAndFilterRange_expectFailed()
      throws ParametersException, ProcessingException {
    tested = createNewStatusCodesComparator("a-lot-of-errors-with-exclude-result.json");

    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);

    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE);
    tested.setParameters(params);

    result = tested.compare();
    assertEquals(ComparatorStepResult.Status.FAILED, result.getStatus());

    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(true);
    when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_MULTIPLE);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE_REMOVE_DEFAULT_RANGE);
    tested.setParameters(params);

    result = tested.compare();
    assertEquals(ComparatorStepResult.Status.FAILED, result.getStatus());

    when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_MULTIPLE);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE);
    tested.setParameters(params);

    result = tested.compare();
    assertEquals(ComparatorStepResult.Status.FAILED, result.getStatus());
  }

  @Test
  public void compareTest_filterCodesAndFilterRange_expectPassed()
      throws ParametersException, ProcessingException {
    tested = createNewStatusCodesComparator("a-lot-of-errors-with-exclude-result.json");

    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(true);
    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);

    when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_SINGLE);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE_REMOVE_DEFAULT_RANGE);
    tested.setParameters(params);

    result = tested.compare();
    assertEquals(Status.PASSED, result.getStatus());
  }

  @Test
  public void compareTest_testExclude_expectPassed()
      throws ParametersException, ProcessingException {
    tested = createNewStatusCodesComparator("a-lot-of-errors-with-exclude-result.json");

    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(true);
    when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_EXCLUDED);
    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE_REMOVE_DEFAULT_RANGE);

    tested.setParameters(params);

    result = tested.compare();
    assertEquals(ComparatorStepResult.Status.PASSED, result.getStatus());
  }

  @Test
  public void compareTest_defaultRange_expectPassed() throws ProcessingException {
    tested = createNewStatusCodesComparator("default-range-399-601-errors-result.json");

    result = tested.compare();
    assertEquals(ComparatorStepResult.Status.PASSED, result.getStatus());
  }

  @Test
  public void compareTest_defaultRange_expectFailed() throws ProcessingException {
    tested = createNewStatusCodesComparator("default-range-400-600-errors-result.json");

    result = tested.compare();
    assertEquals(ComparatorStepResult.Status.FAILED, result.getStatus());
  }

  @Test
  public void setParameters() throws ParametersException {
    tested = createNewStatusCodesComparator("expected-data-200-result.json");

    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE);
    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(true);
    when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_SINGLE);

    // no parameters errors expected - unable to assert anything
    tested.setParameters(params);
  }

  @Test
  public void setParameters_filterRange() throws ParametersException {
    tested = createNewStatusCodesComparator("expected-data-200-result.json");

    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE);

    // no parameters errors expected - unable to assert anything
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_filterRangeInvalid() throws ParametersException {
    tested = createNewStatusCodesComparator("expected-data-200-result.json");

    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE_INVALID);

    // parameters exception expected - unable to assert anything
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_filterRangeNotANumber() throws ParametersException {
    tested = createNewStatusCodesComparator("expected-data-200-result.json");

    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE_NAN);

    // parameters exception expected - unable to assert anything
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_filterRangeUpperBoundLessThanLowerBound() throws ParametersException {
    tested = createNewStatusCodesComparator("expected-data-200-result.json");

    when(params.containsKey(PARAM_FILTER_RANGE)).thenReturn(true);
    when(params.get(PARAM_FILTER_RANGE)).thenReturn(FILTER_RANGE_UPPER_LESS_THAN_LOWER);

    // parameters exception expected - unable to assert anything
    tested.setParameters(params);
  }

  @Test
  public void setParameters_filterCodes() throws ParametersException {
    tested = createNewStatusCodesComparator("expected-data-200-result.json");

    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(true);
    when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_SINGLE);

    // parameters exception expected - unable to assert anything
    tested.setParameters(params);
  }

  @Test
  public void setParameters_filterCodesMultipleValues() throws ParametersException {
    tested = createNewStatusCodesComparator("expected-data-200-result.json");

    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(true);
    when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_MULTIPLE);

    // no parameters errors expected - unable to assert anything
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_filterCodesNotANumber() throws ParametersException {
    tested = createNewStatusCodesComparator("expected-data-200-result.json");

    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(true);
    when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_SINGLE_NAN);

    // parameters exception expected - unable to assert anything
    tested.setParameters(params);
  }

  @Test(expected = ParametersException.class)
  public void setParameters_filterCodesMultipleValuesNotANumber() throws ParametersException {
    tested = createNewStatusCodesComparator("expected-data-200-result.json");

    when(params.containsKey(PARAM_FILTER_CODES)).thenReturn(true);
    when(params.get(PARAM_FILTER_CODES)).thenReturn(FILTER_CODES_MULTIPLE_NAN);

    // parameters exception expected - unable to assert anything
    tested.setParameters(params);
  }

  private StatusCodesComparator createNewStatusCodesComparator(String pathToJson) {
    comparatorProperties = new ComparatorProperties(TEST_COMPANY, TEST_PROJECT, null,
        pathToJson);
    return new StatusCodesComparator(artifactDaoMock, comparatorProperties, new ArrayList<>());
  }

}
