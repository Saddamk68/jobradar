
INSERT INTO ats_platform (NAME, base_url_pattern)
VALUES ('Greenhouse', 'greenhouse.io');

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

INSERT INTO company_ats (company_id, ats_platform_id, ats_job_url) VALUES
((SELECT id FROM company WHERE NAME='Stripe'), 1, 'https://boards.greenhouse.io/stripe'),
((SELECT id FROM company WHERE NAME='Datadog'), 1, 'https://boards.greenhouse.io/datadog'),
((SELECT id FROM company WHERE NAME='Atlassian'), 1, 'https://boards.greenhouse.io/atlassian'),
((SELECT id FROM company WHERE NAME='Coinbase'), 1, 'https://boards.greenhouse.io/coinbase'),
((SELECT id FROM company WHERE NAME='Airbnb'), 1, 'https://boards.greenhouse.io/airbnb'),
((SELECT id FROM company WHERE NAME='Pinterest'), 1, 'https://boards.greenhouse.io/pinterest'),
((SELECT id FROM company WHERE NAME='Reddit'), 1, 'https://boards.greenhouse.io/reddit'),
((SELECT id FROM company WHERE NAME='GitHub'), 1, 'https://boards.greenhouse.io/github'),
((SELECT id FROM company WHERE NAME='Okta'), 1, 'https://boards.greenhouse.io/okta'),
((SELECT id FROM company WHERE NAME='Elastic'), 1, 'https://boards.greenhouse.io/elastic'),

((SELECT id FROM company WHERE NAME='MongoDB'), 1, 'https://boards.greenhouse.io/mongodb'),
((SELECT id FROM company WHERE NAME='Snowflake'), 1, 'https://boards.greenhouse.io/snowflake'),
((SELECT id FROM company WHERE NAME='Twilio'), 1, 'https://boards.greenhouse.io/twilio'),
((SELECT id FROM company WHERE NAME='HashiCorp'), 1, 'https://boards.greenhouse.io/hashicorp'),
((SELECT id FROM company WHERE NAME='DigitalOcean'), 1, 'https://boards.greenhouse.io/digitalocean'),

((SELECT id FROM company WHERE NAME='Confluent'), 1, 'https://boards.greenhouse.io/confluent'),
((SELECT id FROM company WHERE NAME='Razorpay'), 1, 'https://boards.greenhouse.io/razorpay'),
((SELECT id FROM company WHERE NAME='Postman'), 1, 'https://boards.greenhouse.io/postman'),
((SELECT id FROM company WHERE NAME='BrowserStack'), 1, 'https://boards.greenhouse.io/browserstack'),
((SELECT id FROM company WHERE NAME='Freshworks'), 1, 'https://boards.greenhouse.io/freshworks'),

((SELECT id FROM company WHERE NAME='Chargebee'), 1, 'https://boards.greenhouse.io/chargebee'),
((SELECT id FROM company WHERE NAME='Zoho'), 1, 'https://boards.greenhouse.io/zoho'),
((SELECT id FROM company WHERE NAME='Paytm'), 1, 'https://boards.greenhouse.io/paytm'),
((SELECT id FROM company WHERE NAME='PhonePe'), 1, 'https://boards.greenhouse.io/phonepe'),
((SELECT id FROM company WHERE NAME='Swiggy'), 1, 'https://boards.greenhouse.io/swiggy'),

((SELECT id FROM company WHERE NAME='Zomato'), 1, 'https://boards.greenhouse.io/zomato'),
((SELECT id FROM company WHERE NAME='Meesho'), 1, 'https://boards.greenhouse.io/meesho'),
((SELECT id FROM company WHERE NAME='Flipkart'), 1, 'https://boards.greenhouse.io/flipkart'),
((SELECT id FROM company WHERE NAME='Ola'), 1, 'https://boards.greenhouse.io/olacabs'),
((SELECT id FROM company WHERE NAME='CRED'), 1, 'https://boards.greenhouse.io/cred'),

((SELECT id FROM company WHERE NAME='Groww'), 1, 'https://boards.greenhouse.io/groww'),
((SELECT id FROM company WHERE NAME='Upstox'), 1, 'https://boards.greenhouse.io/upstox'),
((SELECT id FROM company WHERE NAME='Dream11'), 1, 'https://boards.greenhouse.io/dream11'),
((SELECT id FROM company WHERE NAME='ShareChat'), 1, 'https://boards.greenhouse.io/sharechat'),
((SELECT id FROM company WHERE NAME='InMobi'), 1, 'https://boards.greenhouse.io/inmobi'),

((SELECT id FROM company WHERE NAME='Rippling'), 1, 'https://boards.greenhouse.io/rippling'),
((SELECT id FROM company WHERE NAME='Deel'), 1, 'https://boards.greenhouse.io/deel'),
((SELECT id FROM company WHERE NAME='HubSpot'), 1, 'https://boards.greenhouse.io/hubspot'),
((SELECT id FROM company WHERE NAME='Notion'), 1, 'https://boards.greenhouse.io/notion'),
((SELECT id FROM company WHERE NAME='Cloudflare'), 1, 'https://boards.greenhouse.io/cloudflare'),

((SELECT id FROM company WHERE NAME='Adobe'), 1, 'https://boards.greenhouse.io/adobe'),
((SELECT id FROM company WHERE NAME='Salesforce'), 1, 'https://boards.greenhouse.io/salesforce'),
((SELECT id FROM company WHERE NAME='VMware'), 1, 'https://boards.greenhouse.io/vmware'),
((SELECT id FROM company WHERE NAME='ServiceNow'), 1, 'https://boards.greenhouse.io/servicenow'),
((SELECT id FROM company WHERE NAME='Nutanix'), 1, 'https://boards.greenhouse.io/nutanix'),

((SELECT id FROM company WHERE NAME='UiPath'), 1, 'https://boards.greenhouse.io/uipath'),
((SELECT id FROM company WHERE NAME='NVIDIA'), 1, 'https://boards.greenhouse.io/nvidia'),
((SELECT id FROM company WHERE NAME='Palantir'), 1, 'https://boards.greenhouse.io/palantir'),
((SELECT id FROM company WHERE NAME='SAP'), 1, 'https://boards.greenhouse.io/sap'),
((SELECT id FROM company WHERE NAME='ThoughtSpot'), 1, 'https://boards.greenhouse.io/thoughtspot');

INSERT INTO target_skill (skill_name, weight)
VALUES
('Java', 1.0),
('Spring Boot', 1.2),
('Kafka', 1.1);

INSERT INTO target_role (role_name, min_experience, max_experience)
VALUES ('Backend Engineer', 4, 8);
