INSERT INTO ats_platform (NAME, base_url_pattern)
VALUES
('Greenhouse', 'greenhouse.io'),
('SmartRecruiters', 'smartrecruiters.com'),
('Workday', 'myworkdayjobs.com'),
('Lever', 'lever.co');

INSERT INTO company (NAME, career_page_url, industry) VALUES
('Stripe', 'https://boards.greenhouse.io/stripe', 'FinTech'),
('Datadog', 'https://boards.greenhouse.io/datadog', 'Observability'),
('Atlassian', 'https://boards.greenhouse.io/atlassian', 'Collaboration'),
('Coinbase', 'https://boards.greenhouse.io/coinbase', 'Crypto'),
('Airbnb', 'https://boards.greenhouse.io/airbnb', 'Marketplace'),
('Pinterest', 'https://boards.greenhouse.io/pinterest', 'Social'),
('Reddit', 'https://boards.greenhouse.io/reddit', 'Social'),
('GitHub', 'https://boards.greenhouse.io/github', 'Developer Tools'),
('Okta', 'https://boards.greenhouse.io/okta', 'Identity'),
('Elastic', 'https://boards.greenhouse.io/elastic', 'Search'),

('MongoDB', 'https://boards.greenhouse.io/mongodb', 'Database'),
('Snowflake', 'https://boards.greenhouse.io/snowflake', 'Data Platform'),
('Twilio', 'https://boards.greenhouse.io/twilio', 'Communication'),
('HashiCorp', 'https://boards.greenhouse.io/hashicorp', 'DevOps'),
('DigitalOcean', 'https://boards.greenhouse.io/digitalocean', 'Cloud'),

('Confluent', 'https://boards.greenhouse.io/confluent', 'Streaming'),
('Razorpay', 'https://boards.greenhouse.io/razorpay', 'FinTech'),
('Postman', 'https://boards.greenhouse.io/postman', 'API Platform'),
('BrowserStack', 'https://boards.greenhouse.io/browserstack', 'Testing'),
('Freshworks', 'https://boards.greenhouse.io/freshworks', 'SaaS'),

('Chargebee', 'https://boards.greenhouse.io/chargebee', 'Billing'),
('Zoho', 'https://boards.greenhouse.io/zoho', 'SaaS'),
('Paytm', 'https://boards.greenhouse.io/paytm', 'FinTech'),
('PhonePe', 'https://boards.greenhouse.io/phonepe', 'FinTech'),
('Swiggy', 'https://boards.greenhouse.io/swiggy', 'Marketplace'),

('Zomato', 'https://boards.greenhouse.io/zomato', 'Marketplace'),
('Meesho', 'https://boards.greenhouse.io/meesho', 'Ecommerce'),
('Flipkart', 'https://boards.greenhouse.io/flipkart', 'Ecommerce'),
('Ola', 'https://boards.greenhouse.io/olacabs', 'Mobility'),
('CRED', 'https://boards.greenhouse.io/cred', 'FinTech'),

('Groww', 'https://boards.greenhouse.io/groww', 'FinTech'),
('Upstox', 'https://boards.greenhouse.io/upstox', 'FinTech'),
('Dream11', 'https://boards.greenhouse.io/dream11', 'Gaming'),
('ShareChat', 'https://boards.greenhouse.io/sharechat', 'Social'),
('InMobi', 'https://boards.greenhouse.io/inmobi', 'AdTech'),

('Rippling', 'https://boards.greenhouse.io/rippling', 'HR Tech'),
('Deel', 'https://boards.greenhouse.io/deel', 'HR Tech'),
('HubSpot', 'https://boards.greenhouse.io/hubspot', 'CRM'),
('Notion', 'https://boards.greenhouse.io/notion', 'Productivity'),
('Cloudflare', 'https://boards.greenhouse.io/cloudflare', 'Networking'),

('Adobe', 'https://boards.greenhouse.io/adobe', 'Software'),
('Salesforce', 'https://boards.greenhouse.io/salesforce', 'CRM'),
('VMware', 'https://boards.greenhouse.io/vmware', 'Virtualization'),
('ServiceNow', 'https://boards.greenhouse.io/servicenow', 'Enterprise'),
('Nutanix', 'https://boards.greenhouse.io/nutanix', 'Cloud'),

('UiPath', 'https://boards.greenhouse.io/uipath', 'Automation'),
('NVIDIA', 'https://boards.greenhouse.io/nvidia', 'Hardware'),
('Palantir', 'https://boards.greenhouse.io/palantir', 'Data'),
('SAP', 'https://boards.greenhouse.io/sap', 'Enterprise'),
('ThoughtSpot', 'https://boards.greenhouse.io/thoughtspot', 'Analytics');

INSERT INTO company_ats (company_id, ats_platform_id, ats_job_url)
SELECT id, 1, career_page_url
FROM company
WHERE career_page_url LIKE '%greenhouse%';

-- for smartrecruiters
INSERT INTO company (name, career_page_url, industry) VALUES
('Bosch', 'https://careers.smartrecruiters.com/BoschGroup', 'Manufacturing'),
('Visa', 'https://careers.smartrecruiters.com/Visa', 'FinTech'),
('IKEA', 'https://careers.smartrecruiters.com/IKEA', 'Retail'),
('Pandora', 'https://careers.smartrecruiters.com/Pandora', 'Retail'),
('Volvo Group', 'https://careers.smartrecruiters.com/VolvoGroup', 'Automotive'),
('Nokia', 'https://careers.smartrecruiters.com/Nokia', 'Telecom'),
('Philips', 'https://careers.smartrecruiters.com/Philips', 'Healthcare'),
('Bosch Global', 'https://careers.smartrecruiters.com/BoschGlobal', 'Manufacturing'),
('Visa Europe', 'https://careers.smartrecruiters.com/VisaEurope', 'FinTech'),
('Pandora Jewelry', 'https://careers.smartrecruiters.com/PandoraJewelry', 'Retail'),

('Publicis Groupe', 'https://careers.smartrecruiters.com/PublicisGroupe', 'Marketing'),
('Capgemini Engineering', 'https://careers.smartrecruiters.com/CapgeminiEngineering', 'Consulting'),
('Vestas', 'https://careers.smartrecruiters.com/Vestas', 'Energy'),
('Bosch Rexroth', 'https://careers.smartrecruiters.com/BoschRexroth', 'Manufacturing'),
('Adidas Digital', 'https://careers.smartrecruiters.com/AdidasDigital', 'Retail'),

('LVMH', 'https://careers.smartrecruiters.com/LVMH', 'Luxury'),
('Hilti', 'https://careers.smartrecruiters.com/Hilti', 'Construction'),
('Takeda', 'https://careers.smartrecruiters.com/Takeda', 'Pharma'),
('Leroy Merlin', 'https://careers.smartrecruiters.com/LeroyMerlin', 'Retail'),
('Swarovski', 'https://careers.smartrecruiters.com/Swarovski', 'Retail'),

('Bosch Automotive', 'https://careers.smartrecruiters.com/BoschAutomotive', 'Automotive'),
('Axa', 'https://careers.smartrecruiters.com/Axa', 'Insurance'),
('Total Energies', 'https://careers.smartrecruiters.com/TotalEnergies', 'Energy'),
('Veolia', 'https://careers.smartrecruiters.com/Veolia', 'Utilities'),
('Danone', 'https://careers.smartrecruiters.com/Danone', 'Food'),

('Schneider Electric', 'https://careers.smartrecruiters.com/SchneiderElectric', 'Energy'),
('Air Liquide', 'https://careers.smartrecruiters.com/AirLiquide', 'Energy'),
('Siemens Mobility', 'https://careers.smartrecruiters.com/SiemensMobility', 'Transport'),
('Bosch Security', 'https://careers.smartrecruiters.com/BoschSecurity', 'Security'),
('Bosch Software', 'https://careers.smartrecruiters.com/BoschSoftware', 'Software'),

('BNP Paribas', 'https://careers.smartrecruiters.com/BNPParibas', 'Banking'),
('Sodexo', 'https://careers.smartrecruiters.com/Sodexo', 'Hospitality'),
('Dassault Systemes', 'https://careers.smartrecruiters.com/DassaultSystemes', 'Software'),
('Carrefour', 'https://careers.smartrecruiters.com/Carrefour', 'Retail'),
('Orange', 'https://careers.smartrecruiters.com/Orange', 'Telecom'),

('Allianz', 'https://careers.smartrecruiters.com/Allianz', 'Insurance'),
('Thales', 'https://careers.smartrecruiters.com/Thales', 'Defense'),
('Alstom', 'https://careers.smartrecruiters.com/Alstom', 'Transport'),
('Capgemini', 'https://careers.smartrecruiters.com/Capgemini', 'Consulting'),
('Accor', 'https://careers.smartrecruiters.com/Accor', 'Hospitality'),

('Sanofi', 'https://careers.smartrecruiters.com/Sanofi', 'Pharma'),
('Renault', 'https://careers.smartrecruiters.com/Renault', 'Automotive'),
('Peugeot', 'https://careers.smartrecruiters.com/Peugeot', 'Automotive'),
('Citroen', 'https://careers.smartrecruiters.com/Citroen', 'Automotive'),
('Valeo', 'https://careers.smartrecruiters.com/Valeo', 'Automotive'),

('Michelin', 'https://careers.smartrecruiters.com/Michelin', 'Automotive'),
('EDF Energy', 'https://careers.smartrecruiters.com/EDF', 'Energy'),
('Engie', 'https://careers.smartrecruiters.com/Engie', 'Energy'),
('Heineken', 'https://careers.smartrecruiters.com/Heineken', 'Beverages'),
('Carlsberg', 'https://careers.smartrecruiters.com/Carlsberg', 'Beverages');

INSERT INTO company_ats (company_id, ats_platform_id, ats_job_url)
SELECT id, 2, career_page_url
FROM company
WHERE career_page_url LIKE '%smartrecruiters%';

-- for workday
INSERT INTO company (name, career_page_url, industry) VALUES
('Amazon', 'https://amazon.jobs', 'Ecommerce'),
('Walmart', 'https://careers.walmart.com', 'Retail'),
('Target', 'https://careers.target.com', 'Retail'),
('Netflix', 'https://jobs.netflix.com', 'Streaming'),
('Tesla', 'https://www.tesla.com/careers', 'Automotive'),

('Apple', 'https://jobs.apple.com', 'Technology'),
('Nike', 'https://jobs.nike.com', 'Retail'),
('Dell', 'https://jobs.dell.com', 'Technology'),
('PayPal', 'https://paypalcareers.com', 'FinTech'),
('Intel', 'https://jobs.intel.com', 'Semiconductors'),

('HP', 'https://jobs.hp.com', 'Technology'),
('Cisco', 'https://jobs.cisco.com', 'Networking'),
('Oracle', 'https://careers.oracle.com', 'Database'),
('IBM', 'https://careers.ibm.com', 'Technology'),
('Google', 'https://careers.google.com', 'Technology'),

('Microsoft', 'https://careers.microsoft.com', 'Technology'),
('Meta', 'https://careers.meta.com', 'Technology'),
('Adobe', 'https://careers.adobe.com', 'Software'),
('Salesforce', 'https://careers.salesforce.com', 'CRM'),
('SAP', 'https://jobs.sap.com', 'Enterprise'),

('Accenture', 'https://careers.accenture.com', 'Consulting'),
('Deloitte', 'https://careers.deloitte.com', 'Consulting'),
('PwC', 'https://careers.pwc.com', 'Consulting'),
('KPMG', 'https://careers.kpmg.com', 'Consulting'),
('EY', 'https://careers.ey.com', 'Consulting'),

('Goldman Sachs', 'https://careers.goldmansachs.com', 'Banking'),
('JPMorgan', 'https://careers.jpmorgan.com', 'Banking'),
('Morgan Stanley', 'https://careers.morganstanley.com', 'Banking'),
('American Express', 'https://careers.americanexpress.com', 'Finance'),
('Mastercard', 'https://careers.mastercard.com', 'FinTech'),

('Uber', 'https://www.uber.com/careers', 'Mobility'),
('Lyft', 'https://www.lyft.com/careers', 'Mobility'),
('Airbnb', 'https://careers.airbnb.com', 'Marketplace'),
('Booking.com', 'https://careers.booking.com', 'Travel'),
('Expedia', 'https://careers.expediagroup.com', 'Travel'),

('Spotify', 'https://www.lifeatspotify.com/jobs', 'Streaming'),
('Snap', 'https://careers.snap.com', 'Social'),
('TikTok', 'https://careers.tiktok.com', 'Social'),
('LinkedIn', 'https://careers.linkedin.com', 'Social'),
('Twitter', 'https://careers.twitter.com', 'Social'),

('eBay', 'https://careers.ebayinc.com', 'Ecommerce'),
('Shopify', 'https://www.shopify.com/careers', 'Ecommerce'),
('Stripe', 'https://stripe.com/jobs', 'FinTech'),
('Square', 'https://careers.squareup.com', 'FinTech'),
('Robinhood', 'https://careers.robinhood.com', 'FinTech'),

('Zoom', 'https://careers.zoom.us', 'Communication'),
('Slack', 'https://slack.com/careers', 'Communication'),
('Dropbox', 'https://jobs.dropbox.com', 'Cloud'),
('Snowflake', 'https://careers.snowflake.com', 'Data'),
('Databricks', 'https://www.databricks.com/company/careers', 'Data');

INSERT INTO company_ats (company_id, ats_platform_id, ats_job_url)
SELECT id, 3, career_page_url
FROM company
WHERE career_page_url NOT LIKE '%greenhouse%'
AND career_page_url NOT LIKE '%smartrecruiters%';

INSERT INTO target_skill (skill_name, weight)
VALUES
('Java', 1.0),
('Spring Boot', 1.2),
('Kafka', 1.1);

INSERT INTO target_role (role_name, min_experience, max_experience)
VALUES ('Backend Engineer', 4, 8);
