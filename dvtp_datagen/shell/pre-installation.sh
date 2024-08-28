echo "Installing 3G Session Browser Datagen..."

# Add the dcuser home directory, user and group

%define dcuser /home/dcuser

if [ ! -d $RPM_BUILD_ROOT%{dcuser} ]; then
	mkdir -p $RPM_BUILD_ROOT%{dcuser}
	%_sbindir/groupadd -g 205 dc5000
	useradd -g dc5000 -d $RPM_BUILD_ROOT%{dcuser} -u 308 dcuser
	chown -R dcuser:dc5000 $RPM_BUILD_ROOT%{dcuser}
fi