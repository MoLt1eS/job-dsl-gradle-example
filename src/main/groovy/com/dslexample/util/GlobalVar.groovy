package com.dslexample.util

/**
 * Created by tiago on 31-07-2018.
 * Contains all the Global most used constants
 *
 * All paths MUST end with character '/' that rule avoids to have code like "A" + "SEPARATOR" + "B"
 */
final class GlobalVar {

    static String CIAB = "CIAB"
    static String CONFIG_DIR = "src/config/"
    static String CONFIG_CIAB_DIR = CONFIG_DIR + "ciab/"

    static String GITHUB_REPO_LOCATION_URL = "https://github.com/compareeuropegroup/%s.git"

    static String GITHUB_CREDENTIALS_ID = "github-1"

    static String[] CIAB_PROJECTS = ["ciab-base, ciab-broadband", "ciab-carinsurance", "ciab-core", "ciab-customer-cloud", "ciab-finance", "ciab-landing", "ciab-learningcenter", "ciab-payment", "ciab-sim", "ciab-telcoproviders", "ciab-travelinsurance", "ciab-views"]

}

