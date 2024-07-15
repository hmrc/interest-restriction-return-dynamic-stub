
# Interest Restriction Return Dynamic Stub

This is a protected backend microservice that stubs the functionality of the HMRC HoD systems API for Interest Restriction Returns.

## Running the tests
```bash
./run_all_tests.sh
```

## API Endpoint Definitions

**Manage the Reporting Company for the Interest Restriction Return filing**

- [Appoint a Reporting Company](#Appoint-a-Reporting-Company)
- [Revoke a Reporting Company](#Revoke-a-Reporting-Company)

**File the Interest Restriction Return**

- [Submit Abbreviated Return](#Submit-an-Abbreviated-Interest-Restriction-Return)
- [Submit Full Return](#Submit-a-Full-Interest-Restriction-Return)

### Appoint a Reporting Company

**URL:** `/organisations/interest-restrictions-return/appoint`

**Method:** `POST`

**Authentication required:** `Yes, bearer token required`

**Request Schema:** [Json Schema](conf/resources/schemas/appoint_irr_reporting_company.json)

**Example Request**
```json
{
  "agentDetails": {
    "agentActingOnBehalfOfCompany": true,
    "agentName": "AAgent"
  },
  "reportingCompany": {
    "companyName": "ACompany",
    "ctutr": "1123456789",
    "sameAsUltimateParent": true
  },
  "isReportingCompanyAppointingItself": false,
  "identityOfAppointingCompany": {
    "companyName": "BCompany",
    "countryOfIncorporation": "UK",
    "ctutr": "9999999999",
    "legalEntityIdentifier": "Z29900T8BM49AURSDO53"
  },
  "authorisingCompanies": [
    {
      "companyName": "CCompany",
      "utr": "5555555555",
      "consenting": true
    },
    {
      "companyName": "D-Company, with unusual chars & % ! | ~ #",
      "utr": "1122334455",
      "consenting": true
    }
  ],
  "ultimateParentCompany": {
    "isUk": true,
    "companyName": "E*Company$$",
    "ctutr": "0000000000",
    "sautr": "1237658890",
    "knownAs": "655 Squadron",
    "countryOfIncorporation": "ZA",
    "legalEntityIdentifier": "549300QKDHYRRQH2MB86"
  },
  "accountingPeriod": {
    "startDate": "2018-11-01",
    "endDate": "2019-12-01"
  },
  "declaration": true
}
```

#### Success Response

**Code:** `201 (CREATED)`

**Response Schema**
```json
{
    "type": "object",
    "properties": {
        "acknowledgementReference": {
            "type": "string"
        }   
    }   
}
```

**Example Response**

Status: `201 (CREATED)`
```json
{
    "acknowledgementReference": "ABC1234"
}
```


### Revoke a Reporting Company


**URL:** `/organisations/interest-restrictions-return/revoke`

**Method:** `POST`

**Authentication required:** `Yes, bearer token required`

**Request Schema:** [Json Schema](conf/resources/schemas/revoke_irr_reporting_company.json)

**Example Request**
```json
{
  "agentDetails": {
    "agentActingOnBehalfOfCompany": true,
    "agentName": "AAgent"
  },
  "reportingCompany": {
    "companyName": "ACompany",
    "ctutr": "1123456789",
    "sameAsUltimateParent": true
  },
  "isReportingCompanyRevokingItself": false,
  "companyMakingRevocation": {
    "companyName": "BCompany",
    "countryOfIncorporation": "UK",
    "ctutr": "9999999999",
    "legalEntityIdentifier": "Z29900T8BM49AURSDO53"
  },
  "authorisingCompanies": [
    {
      "companyName": "CCompany",
      "utr": "5555555555",
      "consenting": true
    },
    {
      "companyName": "D-Company, with unusual chars & % ! | ~ #",
      "utr": "1122334455",
      "consenting": true
    }
  ],
  "ultimateParentCompany": {
    "isUk": true,
    "companyName": "E*Company$$",
    "ctutr": "0000000000",
    "sautr": "1237658890",
    "knownAs": "Mabel",
    "countryOfIncorporation": "ZA",
    "legalEntityIdentifier": "549300QKDHYRRQH2MB86"
  },
  "accountingPeriod": {
    "startDate": "2018-11-01",
    "endDate": "2019-12-01"
  },
  "declaration": true
}
```

#### Success Response

**Code:** `201 (CREATED)`

**Response Schema**
```json
{
    "type": "object",
    "properties": {
        "acknowledgementReference": {
            "type": "string"
        }   
    }   
}
```

**Example Response**

Status: `201 (CREATED)`
```json
{
    "acknowledgementReference": "ABC1234"
}
```


### Submit an Abbreviated Interest Restriction Return

**URL:** `/organisations/interest-restrictions-return/abbreviated`

**Method:** `POST`

**Authentication required:** `Yes, bearer token required`

**Request Schema:** [Json Schema](conf/resources/schemas/abbreviated_irr.json)

**Example Request**
```json
   {
	"agentDetails": {
		"agentActingOnBehalfOfCompany": true,
		"agentName": "Some agent"
	},
	"reportingCompany": {
		"companyName": "A Reporting Company",
		"ctutr": "1123456747",
		"sameAsUltimateParent": true
	},
	"parentCompany": {
		"ultimateParent": {
			"isUk": true,
			"ctutr": "8764783632",
			"sautr": "0897876656",
			"knownAs": "$$=!|~Ḁỿ",
			"legalEntityIdentifier": "213800WRYCD72WGOOY58",
			"companyName": "$$=!|~Ḁỿ",
			"countryOfIncorporation": "JP"
		}
	},
	"publicInfrastructure": true,
	"groupCompanyDetails": {
		"totalCompanies": 1,
		"accountingPeriod": {
			"startDate": "1920-02-29",
			"endDate": "1920-02-29"
		}
	},
	"submissionType": "original",
	"revisedReturnDetails": "Calculation symbols and formulae y=x,  73463784 ÷ (839393987 + 73678979)",
	"angie": 0.01,
	"groupLevelElections": {
		"groupRatio": {
			"isElected": true,
			"groupRatioBlended": {
				"isElected": true,
				"investorGroups": [{
						"groupName": "₠ ₡ ₢ ₣ ₤ ₥ ₦ ₧ ₨ ₩ ₪ ₫ € ₭ ₮ ₯ ₰ ₱ ₲",
						"elections": [
							"groupRatioBlended",
							"groupEBITDA",
							"interestAllowanceAlternativeCalculation",
							"interestAllowanceNonConsolidatedInvestment",
							"interestAllowanceConsolidatedPartnership"
						]
					},
					{
						"groupName": " ₰ ₱ ₲ ₳ ₴ ₵ ₶ ₷ ₸ ₹ ₺ ₻ ₼ ₽ ₾ ₿"
					}
				]
			},
			"groupEBITDAChargeableGains": true
		},
		"interestAllowanceAlternativeCalculation": true,
		"interestAllowanceNonConsolidatedInvestment": {
			"isElected": true,
			"nonConsolidatedInvestments": [{
					"investmentName": "Investment 12323"
				},
				{
					"investmentName": "Investment C"
				}
			]
		},
		"interestAllowanceConsolidatedPartnership": {
			"isElected": true,
			"consolidatedPartnerships": [{
					"partnershipName": "Öbs",
					"sautr": "3466564633"
				},
				{
					"partnershipName": "Äßß",
					"sautr": "9495644574"
				}
			]
		}
	},
	"ukCompanies": [{
			"companyName": "!!Company A$$$==",
			"utr": "7634834546",
			"consenting": true,
			"qicElection": true
		},
		{
			"companyName": "!!Company B $$$==",
			"utr": "7236453286",
			"consenting": true,
			"qicElection": true
		}
	]
}
```

#### Success Response

**Code:** `201 (CREATED)`

**Response Schema**
```json
{
    "type": "object",
    "properties": {
        "acknowledgementReference": {
            "type": "string"
        }   
    }   
}
```

**Example Response**

Status: `201 (CREATED)`
```json
{
    "acknowledgementReference": "ABC1234"
}
```


### Submit a Full Interest Restriction Return

**URL:** `/organisations/interest-restrictions-return/full `

**Method:** `POST`

**Authentication required:** `Yes, bearer token required`

**Request Schema:** [Json Schema](conf/resources/schemas/submit_full_irr.json)

**Example Request**
```json
{
    "agentDetails": {
      "agentActingOnBehalfOfCompany": true,
      "agentName": "Some agent"
    },
    "reportingCompany": {
      "companyName": "A Reporting Company",
      "ctutr": "1123456747",
      "sameAsUltimateParent": true
    },
    "parentCompany": {
      "ultimateParent": {
        "isUk": true,
        "ctutr": "8764783632",
        "sautr": "0897876656",
        "knownAs": "$$=!|~Ḁỿ",
        "legalEntityIdentifier": "549300D178N8UFD6I259",
        "companyName": "$$=!|~Ḁỿ",
        "countryOfIncorporation": "JP"
      }
    },
    "publicInfrastructure": true,
    "groupCompanyDetails": {
      "totalCompanies": 1,
      "accountingPeriod": {
        "startDate": "1920-02-29",
        "endDate": "1920-02-29"
      }
    },
    "submissionType": "original",
    "revisedReturnDetails": "Calculation symbols and formulae y=x,  73463784 ÷ (839393987 + 73678979)",
    "angie": 0,
    "returnContainsEstimates": true,
    "groupEstimateReason": "Having trouble with multiplication ×, division ÷, and money ₠ ₡ ₢ ₣ ₤ ₥ ₦ ₧ ₨ ₩ ₪ ₫ € ₭ ₮ ₯ ₰ ₱ ₲ ₳ ₴ ₵ ₶ ₷ ₸ ₹ ₺ ₻ ₼ ₽ ₾ ₿",
    "companiesEstimateReason": "%%Some company names are ≣ to each other © ±!%%",
    "groupSubjectToInterestRestrictions": true,
    "groupSubjectToInterestReactivation": true,
    "revisedReturnDifferences": "≈ ≤≥",
    "groupLevelElections": {
      "groupRatio": {
        "isElected": true,
        "groupRatioBlended": {
          "isElected": true,
          "investorGroups": [
            {
              "groupName": "₠ ₡ ₢ ₣ ₤ ₥ ₦ ₧ ₨ ₩ ₪ ₫ € ₭ ₮ ₯ ₰ ₱ ₲",
              "elections": [
                "groupRatioBlended",
                "groupEBITDA",
                "interestAllowanceAlternativeCalculation",
                "interestAllowanceNonConsolidatedInvestment",
                "interestAllowanceConsolidatedPartnership"
              ]
            },
            {
              "groupName": " ₰ ₱ ₲ ₳ ₴ ₵ ₶ ₷ ₸ ₹ ₺ ₻ ₼ ₽ ₾ ₿",
              "elections": [
                "groupRatioBlended",
                "groupEBITDA"
              ]
            }
          ]
        },
        "groupEBITDAChargeableGains": true
      },
      "interestAllowanceAlternativeCalculation": true,
      "interestAllowanceNonConsolidatedInvestment": {
        "isElected": true,
        "nonConsolidatedInvestments": [
          {
            "investmentName": "Investment 12323"
          },
          {
            "investmentName": "Investment C"
          }
        ]
      },
      "interestAllowanceConsolidatedPartnership": {
        "isElected": true,
        "consolidatedPartnerships": [
          {
            "partnershipName": "Öbs",
            "sautr": "3466564633"
          },
          {
            "partnershipName": "Äßß",
            "sautr": "9495644574"
          }
        ]
      }
    },
    "ukCompanies": [
      {
        "companyName": "!!Company A$$$==",
        "companyEstimateReason": "?",
        "utr": "7634834546",
        "consenting": true,
        "qicElection": true,
        "netTaxInterestExpense": 378463784.78,
        "netTaxInterestIncome": 36486348345.5,
        "taxEBITDA": 9.99,
        "allocatedRestrictions": {
          "ap1EndDate": "2021-02-28",
          "ap2EndDate": "2022-04-30",
          "ap3EndDate": "2022-12-31",
          "disallowanceAp1": 789.88,
          "disallowanceAp2": 67830,
          "disallowanceAp3": 2323,
          "totalDisallowances": 43667.01
        },
        "allocatedReactivations": {
          "currentPeriodReactivation": 2222.5
        }
      },
      {
        "companyName": "!!Company B $$$==",
        "utr": "7236453286",
        "consenting": true,
        "qicElection": true,
        "netTaxInterestExpense": 0,
        "netTaxInterestIncome": 0,
        "taxEBITDA": 1.01,
        "allocatedRestrictions": {
          "ap1EndDate": "1920-02-29",
          "ap2EndDate": "1920-02-29",
          "ap3EndDate": "1920-02-29",
          "disallowanceAp1": 0,
          "disallowanceAp2": 0,
          "disallowanceAp3": 0,
          "totalDisallowances": 0
        },
        "allocatedReactivations": {
          "currentPeriodReactivation": 0
        }
      }
    ],
    "groupLevelAmount": {
      "interestReactivationCap": 4834312,
      "interestAllowanceBroughtForward": 0,
      "interestAllowanceForPeriod": 0,
      "interestCapacityForPeriod": 0
    },
    "adjustedGroupInterest": {
      "qngie": 0,
      "groupEBITDA": 1,
      "groupRatio": 0.00005
    },
    "totalReactivation": 13324234.54,
    "totalRestrictions": 23324234.54
  }

```

#### Success Response

**Code:** `201 (CREATED)`

**Response Schema**
```json
{
    "type": "object",
    "properties": {
        "acknowledgementReference": {
            "type": "string"
        }   
    }   
}
```

**Example Response**

Status: `201 (CREATED)`
```json
{
    "acknowledgementReference": "ABC1234"
}
```


## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
