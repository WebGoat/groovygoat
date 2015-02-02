if (!session) {
  session = request.getSession(true)
}

request['today'] = new Date();
out.println(request.getLocale());
request['locale'] = request.getLocale();
view = "home";