
# Interest Restriction Return Dynamic Stub

This is a protected backend microservice that stubs the functionality of the HMRC HoD systems API for Interest Restriction Returns.

## Running the stub locally
```bash
sm2 --start IRR_ALL
sm2 --stop IRR_DYNAMIC_STUB
sbt run
```

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

**Example Request:** [Request Json](conf/resources/examples/example_appoint_irr_reporting_company_body.json)

#### Success Response

**Code:** `201 (CREATED)`

**Response Schema:** [Json Schema](conf/resources/schemas/response.json)

**Example Response:** [Response Json](conf/resources/examples/example_response.json)

### Revoke a Reporting Company

**URL:** `/organisations/interest-restrictions-return/revoke`

**Method:** `POST`

**Authentication required:** `Yes, bearer token required`

**Request Schema:** [Json Schema](conf/resources/schemas/revoke_irr_reporting_company.json)

**Example Request:** [Request Json](conf/resources/examples/example_revoke_irr_reporting_company_body.json)

#### Success Response

**Code:** `201 (CREATED)`

**Response Schema:** [Json Schema](conf/resources/schemas/response.json)

**Example Response:** [Response Json](conf/resources/examples/example_response.json)

### Submit an Abbreviated Interest Restriction Return

**URL:** `/organisations/interest-restrictions-return/abbreviated`

**Method:** `POST`

**Authentication required:** `Yes, bearer token required`

**Request Schema:** [Json Schema](conf/resources/schemas/abbreviated_irr.json)

**Example Request:** [Request Json](conf/resources/examples/example_abbreviated_irr_body.json)

#### Success Response

**Code:** `201 (CREATED)`

**Response Schema:** [Json Schema](conf/resources/schemas/response.json)

**Example Response:** [Response Json](conf/resources/examples/example_response.json)

### Submit a Full Interest Restriction Return

**URL:** `/organisations/interest-restrictions-return/full `

**Method:** `POST`

**Authentication required:** `Yes, bearer token required`

**Request Schema:** [Json Schema](conf/resources/schemas/submit_full_irr.json)

**Example Request:** [Request Json](conf/resources/examples/example_submit_full_irr_body.json)

#### Success Response

**Code:** `201 (CREATED)`

**Response Schema:** [Json Schema](conf/resources/schemas/response.json)

**Example Response:** [Response Json](conf/resources/examples/example_response.json)

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
