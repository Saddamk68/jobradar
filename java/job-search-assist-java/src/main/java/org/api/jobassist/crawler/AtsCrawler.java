package org.api.jobassist.crawler;

import org.api.jobassist.entity.AtsPlatform;
import org.api.jobassist.entity.Company;
import org.api.jobassist.entity.JobPosting;

import java.util.List;

public interface AtsCrawler {

    List<JobPosting> crawl(String atsJobUrl, Company company, AtsPlatform platform);

}
