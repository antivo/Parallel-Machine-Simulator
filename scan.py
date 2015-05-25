def _scan(f, it, state=None):
  for x in it:
    if state is None:
      state = x
    else:
      state = f(state, x)
    yield state

def scan(f, it, state=None):
    return list(_scan(f, it, state))