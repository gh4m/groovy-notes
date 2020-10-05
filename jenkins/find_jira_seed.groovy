
List jira_version_list = [
"ABC_1.0_int_2.1",
"ABC-1.0",
"ABC-1.0-int-1.0",
"ABC-1.0-int-1.1",
"ABC_1.0_int_3.0",
"ABC_1.0_int_3.0.1",
"ABC_1.0_int_3.1",
"ABC-1.0-int-2.0",
"ABC-1.1",
]

String jira_version_new = "ABC_1.0_int_4.0"

// =============================================

// def function(jira_version_list = [], jira_version_new = "")

String jira_version_seed = "NOT_FOUND"

if (!jira_version_new) {
    println "ERROR: no jira_version_new passed to compare"
    return false
}
if (jira_version_list.isEmpty()) {
    println "passed empty list, returning jira_version_seed = " + jira_version_seed
    return jira_version_seed
}

// convert jira_version_new to map for compare
Map jira_version_new_map = [:]
jira_version_new_map = [:]
jira_version_new_map['compare_to_string'] = jira_version_new.toUpperCase().replaceAll(/\W/, "_")
jira_version_new_map['compare_to_chars'] = []
for (char ch : jira_version_new_map['compare_to_string'].toCharArray()) {
  jira_version_new_map['compare_to_chars'].add(ch)
}

// convert jira_version_list to map for compare
Map jira_version_list_map = [:]
for (current_list_version in jira_version_list) {
  jira_version_list_map[current_list_version] = [:]
  jira_version_list_map[current_list_version]['compare_to_string'] = current_list_version.toUpperCase().replaceAll(/\W/, "_")
  jira_version_list_map[current_list_version]['prev_match_flag'] = false
  jira_version_list_map[current_list_version]['curr_match_flag'] = true
  jira_version_list_map[current_list_version]['compare_to_chars'] = []
  for (char ch : jira_version_list_map[current_list_version]['compare_to_string'].toCharArray()) {
    jira_version_list_map[current_list_version]['compare_to_chars'].add(ch)
  }
  // sanity check, make sure new jira version is not in jira_version_list
  if (jira_version_list_map[current_list_version]['compare_to_string'] == jira_version_new_map['compare_to_string']) {
    jira_version_seed = "FOUND_EXACT"
    println "passed list contains jira_version_new, returning jira_version_seed = " + jira_version_seed
    return jira_version_seed
  }
}

// set when only one match is left or zero matches are left
Boolean match_check_finished = false
List<String> jira_version_match_list_prev = []
List<String> jira_version_match_list_curr = []
// iterate over jira_version_new chars and compare with jira_version_list versions
jira_version_new_map['compare_to_chars'].eachWithIndex { item, index ->
  println "========================================================================"
  println "========================================================================"
  println "checking index=char " + index + "=" + item
  // no further checks if flag set
  if (match_check_finished) {
    println "match_check_finished is set, returning"
    return
  }
  // save jira_version_match_list_curr to jira_version_match_list_prev
  jira_version_match_list_prev = jira_version_match_list_curr.collect()
  jira_version_match_list_curr = []
  // compare current jira_version_new char to respective char in each jira_version_list
  for (current_list_version in jira_version_list) { // for each jira_version_list
    println "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
    // only compare if curr_match_flag = true
    if (!jira_version_list_map[current_list_version]['curr_match_flag']) {
      println "prev match was false skipping " + current_list_version
      continue
    }
    // save curr_match_flag to prev_match_flag
    jira_version_list_map[current_list_version]['prev_match_flag'] = jira_version_list_map[current_list_version]['curr_match_flag']
    // compare the chars
    println "comparing " + current_list_version
    if (jira_version_list_map[current_list_version]['compare_to_chars'][index] == item) {
      jira_version_list_map[current_list_version]['curr_match_flag'] = true
      jira_version_match_list_curr.add(jira_version_list_map[current_list_version]['compare_to_string'])
    } else {
      jira_version_list_map[current_list_version]['curr_match_flag'] = false
    }
    println "jira_version_list_map[current_list_version]['curr_match_flag'] " + jira_version_list_map[current_list_version]['curr_match_flag']
  } // for each jira_version_list
  // any matches?
  println "jira_version_match_list_curr = " + jira_version_match_list_curr
  if (jira_version_match_list_curr.size() <= 1) {
    println "we are done with match check"
    match_check_finished = true
  }
} // eachWithIndex jira_version_new_map

// if 0, copy and use the prev list
if (jira_version_match_list_curr.size() == 0) {
  jira_version_match_list_curr = jira_version_match_list_prev.collect()
}

// sanity check, no dups, we will not know which of the dups to use as seed
List jira_version_match_list_curr_no_dups = jira_version_match_list_curr.unique(false)
if (jira_version_match_list_curr_no_dups.size() != jira_version_match_list_curr.size()) {
  println "ERROR: jira_version_match_list has duplicates"
  return false
}

// sort list and return best seed json
println "sorted " + jira_version_match_list_curr.sort()
println "last item " + jira_version_match_list_curr[-1]
// find actual value
for (current_list_version in jira_version_list) {
  if (jira_version_list_map[current_list_version]['compare_to_string'] == jira_version_match_list_curr[-1]) {
    jira_version_seed = current_list_version
  }
}

// got it, return it
println "found jira_version_seed " + jira_version_seed
return jira_version_seed
