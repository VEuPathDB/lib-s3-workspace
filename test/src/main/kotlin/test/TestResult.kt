package test

class TestResult {

  var successes = 0
  var failures  = 0

  operator fun plusAssign(res: Boolean) {
    if (res)
      successes++
    else
      failures++
  }

  operator fun plusAssign(res: TestResult) {
    successes += res.successes
    failures  += res.failures
  }
}