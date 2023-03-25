import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class App extends JFrame{
    JPopupMenu pm;
	JMenuItem pm_item, pm_item1;
	JPanel Fpanel, Calpanel, Expanel;
	JButton RecomBt;
	JLabel TimeTxt;
	JLabel[][] CalLabel;
	Color[][] CalColor;
	String urlPath = "https://weather.com/ko-KR/weather/tenday/l/1b710466f79872d2ad496c6e09f2b743995e338fdcf8e261d1e38df079e8a87c?unit=m";
	String pageContents = "";
	StringBuilder contents;
	int year, month, day, hour, min, sec, oyear, omonth, oday, ohour, omin, osec;

	int cnt, keep;
	String[] Day = new String[31];
	String Time = "";
	int[] Hum = new int[31];
	int[] Tem = new int[31];
	int[] NoDay;
	double[] Discom = new double[31];

	public App() {
		initializeComponents();
	}

	public static Integer getFirstWeek(Integer year, Integer month) {
		Calendar cal = Calendar.getInstance();

		cal.set(year, month - 1, 1);

		int dayNum = cal.get(Calendar.DAY_OF_WEEK);

		return dayNum - 1;
	}

	public static Integer getLastDay(Integer year, Integer month) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 1);

		int lastDay = cal.getActualMaximum(Calendar.DATE);

		return lastDay;
	}

	private void initializeComponents() {
		Container c = this.getContentPane();
		setTitle("불쾌지수를 구해 가장 쾌적한 날짜를 추천해주는 프로그램");
		c.removeAll();
		c.setLayout(new CardLayout());

		MouseEventHandler handlerObj = new MouseEventHandler();

		pm = new JPopupMenu("Edit");
		pm_item = new JMenuItem("색상 바꾸기");
		pm_item1 = new JMenuItem("원래 색상으로 바꾸기");
		pm.add(pm_item).addMouseListener(handlerObj);
		pm.add(pm_item1).addMouseListener(handlerObj);

		Fpanel = new JPanel();
		Fpanel.setBackground(Color.WHITE);
		Calpanel = new JPanel();
		Calpanel.setBackground(Color.WHITE);
		Expanel = new JPanel();
		Expanel.setBackground(Color.WHITE);

		TimeTxt = new JLabel("");
		TimeTxt.setHorizontalAlignment(JLabel.CENTER);
		TimeTxt.setFont(new Font("Serif", Font.BOLD, 20));

		RecomBt = new JButton("날짜 추천받기");
		RecomBt.addMouseListener(handlerObj);

		CalLabel = new JLabel[9][7];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 7; j++) {
				CalLabel[i][j] = new JLabel("");
				CalLabel[i][j].setHorizontalAlignment(JLabel.CENTER);
				CalLabel[i][j].add(pm);
				CalLabel[i][j].addMouseListener(handlerObj);
			}
		}

		CalLabel[0][0].setText("일(SUN)");
		CalLabel[0][1].setText("월(MON)");
		CalLabel[0][2].setText("화(TUE)");
		CalLabel[0][3].setText("수(WED)");
		CalLabel[0][4].setText("목(THU)");
		CalLabel[0][5].setText("금(FRI)");
		CalLabel[0][6].setText("토(SAT)");

		CalColor = new Color[9][7];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 7; j++) {
				CalColor[i][j] = new Color(255, 255, 255);
			}
		}

		c.add(Fpanel);

		Fpanel.setLayout(new BorderLayout());

		Fpanel.add(Expanel, BorderLayout.NORTH);
		Fpanel.add(Calpanel, BorderLayout.CENTER);
		Calpanel.setLayout(new GridLayout(9, 7, 3, 3));
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 7; j++) {
				Calpanel.add(CalLabel[i][j]);
			}
		}
		Expanel.setLayout(new BorderLayout());
		Expanel.add(TimeTxt, BorderLayout.CENTER);
		Expanel.add(RecomBt, BorderLayout.EAST);

		this.setSize(500, 500);
		this.setLocation(0, 0);
		this.setVisible(true);

		runAwayTime();
	}

	void runAwayTime() {
		// 실행간격 지정(1초)
		int sleepSec = 1;

		// 주기적인 작업을 위한
		final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

		exec.scheduleAtFixedRate(new Runnable() {

			public void run() {
				try {
					Calendar cal = Calendar.getInstance();

					year = cal.get(Calendar.YEAR);
					month = cal.get(Calendar.MONTH) + 1;
					day = cal.get(Calendar.DAY_OF_MONTH);
					hour = cal.get(Calendar.HOUR_OF_DAY);
					min = cal.get(Calendar.MINUTE);
					sec = cal.get(Calendar.SECOND);

					String str = "";
					str = year + "년 " + month + "월 " + day + "일";

					TimeTxt.setText(str);

					if (omonth != month) {
						for (int i = 7 + getFirstWeek(year, month) - 1,
								j = getLastDay(year, month - 1); i >= 7; i--, j--) {
							int p = i / 7, q = i % 7;
							CalLabel[p][q].setText(Integer.toString(j));
							CalLabel[p][q].setOpaque(true);
							CalColor[p][q] = Color.white;
							CalLabel[p][q].setBackground(CalColor[p][q]);
							CalLabel[p][q].setBorder(new LineBorder(new Color(200, 200, 200)));
							CalLabel[p][q].setForeground(new Color(200, 200, 200));
						}
						for (int i = 7 + getFirstWeek(year, month), j = 1; j <= getLastDay(year, month); i++, j++) {
							int p = i / 7, q = i % 7;
							CalLabel[p][q].setText(Integer.toString(j));
							CalLabel[p][q].setOpaque(true);
							CalColor[p][q] = new Color(245, 245, 245);
							CalLabel[p][q].setBackground(CalColor[p][q]);
							CalLabel[p][q].setBorder(new LineBorder(Color.BLACK));
							CalLabel[p][q].setForeground(Color.BLACK);
						}
						for (int i = 7 + getFirstWeek(year, month) + getLastDay(year, month), j = 1; i < 63; i++, j++) {
							int p = i / 7, q = i % 7;
							CalLabel[p][q].setText(Integer.toString(j));
							CalLabel[p][q].setOpaque(true);
							CalColor[p][q] = Color.WHITE;
							CalLabel[p][q].setBackground(CalColor[p][q]);
							CalLabel[p][q].setBorder(new LineBorder(new Color(200, 200, 200)));
							CalLabel[p][q].setForeground(new Color(200, 200, 200));
						}
					}
					if (oday != day) {
						int p, q, b, d, re;
						re = day - 1 + getFirstWeek(year, month) + 7;
						b = (re - 1) / 7;
						d = (re - 1) % 7;
						p = re / 7;
						q = re % 7;
						if (Integer.parseInt(CalLabel[b][d].getText()) + 1 == Integer
								.parseInt(CalLabel[p][q].getText())) {
							CalColor[b][d] = new Color(245, 245, 245);
							CalLabel[b][d].setBackground(CalColor[b][d]);
						}
						CalColor[p][q] = Color.YELLOW;
						CalLabel[p][q].setBackground(CalColor[p][q]);
					}

					oyear = year;
					omonth = month;
					oday = day;
					ohour = hour;
					omin = min;
					osec = sec;
				} catch (Exception e) {
					exec.shutdown();
				}
			}
		}, 0, sleepSec, TimeUnit.SECONDS);
	}

	public static void main(String[] args) {
		App app = new App();	
	}

	public void NoMySet() {
		try {
			OutputStream output = new FileOutputStream(".\\MySet.txt");
			String str = "AM:1\r\n" + "PM:1\r\n" + "Day:1:1:1:1:1:1:1:1:1:1:1:1:1:1:1";
			byte[] by = str.getBytes();
			output.write(by);
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	// enum ChkStr {RED, ORANGE}
	public class SetDialog extends JFrame implements MouseListener {
		ButtonGroup DayGroup;
		JPanel AorP, DayPanel, BtPanel;
		JCheckBox AMBox, PMBox;
		JLabel[] DayLabel;
		JButton SaveBt, InfoBt;

		int[] ChkInt = new int[20];
		String ChkStr[] = { "AM", "PM", "Day" };

		// enum
		public SetDialog(String[] Days) {
			while (true) {
				try {
					File gradeFile = new File(".\\MySet.txt");

					Scanner inFile = new Scanner(gradeFile);

					try {
						inFile.hasNextLine();
						String line = inFile.nextLine();
						Scanner s = new Scanner(line);
						s.useDelimiter("\\s*:\\s*");
						if (!ChkStr[0].equals(s.next()))
							throw new Exception();
						ChkInt[0] = s.nextInt();
						if (ChkInt[0] != 1 && ChkInt[0] != 0) {
							NoMySet();
							continue;
						}
					} catch (Exception e) {
						NoMySet();
						continue;
					}

					try {
						inFile.hasNextLine();
						String line = inFile.nextLine();
						Scanner s = new Scanner(line);
						s.useDelimiter("\\s*:\\s*");
						if (!ChkStr[1].equals(s.next()))
							throw new Exception();
						ChkInt[1] = s.nextInt();
						if (ChkInt[1] != 1 && ChkInt[1] != 0) {
							NoMySet();
							continue;
						}
					} catch (Exception e) {
						NoMySet();
						continue;
					}

					try {
						inFile.hasNextLine();
						String line = inFile.nextLine();
						String[] st = line.split("[:]+");
						if (!ChkStr[2].equals(st[0])) {
							NoMySet();
							continue;
						}
						for (int i = 1; i < 16; i++) {
							ChkInt[i + 1] = Integer.parseInt(st[i]);
							if (ChkInt[i + 1] != 0 && ChkInt[i + 1] != 1) {
								NoMySet();
								continue;
							}
						}

					} catch (Exception e) {
						NoMySet();
						continue;
					}

				} catch (IOException e) {
					NoMySet();
					continue;
				}
				break;
			}
			setTitle("시간 및 날짜 설정");

			JPanel Mset = new JPanel();
			setContentPane(Mset);
			JPanel ConPanel = new JPanel();

			String[] TemTxt = new String[101];
			String[] HumTxt = new String[101];

			AorP = new JPanel();
			AorP.setBorder(new TitledBorder("가능한 시간대"));
			AMBox = new JCheckBox("오전");
			PMBox = new JCheckBox("오후");
			if (ChkInt[0] == 1)
				AMBox.setSelected(true);
			if (ChkInt[1] == 1)
				PMBox.setSelected(true);

			Calendar cal = Calendar.getInstance();

			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);

			DayPanel = new JPanel();
			DayPanel.setLayout(new GridLayout(4, 7, 3, 3));
			DayPanel.setBorder(new TitledBorder("가능한 날짜"));
			DayLabel = new JLabel[28];
			for (int i = 0; i < 7; i++) {
				DayLabel[i] = new JLabel();
				DayLabel[i].setHorizontalAlignment(JLabel.CENTER);
				DayLabel[i].setOpaque(true);
			}
			for (int i = 7; i < 28; i++) {
				DayLabel[i] = new JLabel();
				DayLabel[i].setHorizontalAlignment(JLabel.CENTER);
				DayLabel[i].setOpaque(true);
				DayLabel[i].setBorder(new LineBorder(Color.BLACK));
				DayLabel[i].setForeground(Color.BLACK);
			}
			String BoxTxt = "";
			DayLabel[0].setText("일(SUN)");
			DayLabel[1].setText("월(MON)");
			DayLabel[2].setText("화(TUE)");
			DayLabel[3].setText("수(WED)");
			DayLabel[4].setText("목(THU)");
			DayLabel[5].setText("금(FRI)");
			DayLabel[6].setText("토(SAT)");

			int today = (getFirstWeek(year, month) + day - 1) % 7;

			for (int i = 7 + today; i < today + 7 + 15; i++) {
				if(day + i - 7 - today <= getLastDay(year, month)) {
					DayLabel[i].setText(Integer.toString(day + i - 7 - today) + "일");
					DayLabel[i].addMouseListener(this);
				} 
				else {
					DayLabel[i].setText(Integer.toString((day + i - 7 - today) - getLastDay(year,month)) + "일");
					DayLabel[i].addMouseListener(this);
				}
			}
			for (int i = today + 7; i < today + 7 + 15; i++) {
				if (ChkInt[i - today - 7 + 2] == 1) {
					DayLabel[i].setBackground(new Color(159, 216, 251));
				} else {
					DayLabel[i].setBackground(new Color(255, 182, 193));
				}
			}

			BtPanel = new JPanel();
			SaveBt = new JButton("확인");
			SaveBt.addMouseListener(this);
			InfoBt = new JButton("도움말");
			InfoBt.addMouseListener(this);

			Mset.setLayout(new BorderLayout());
			Mset.add(ConPanel, BorderLayout.CENTER);
			Mset.add(BtPanel, BorderLayout.SOUTH);
			ConPanel.setLayout(new BorderLayout());
			ConPanel.add(AorP, BorderLayout.NORTH);
			ConPanel.add(DayPanel, BorderLayout.CENTER);
			BtPanel.setLayout(new BorderLayout());
			BtPanel.add(SaveBt, BorderLayout.CENTER);
			BtPanel.add(InfoBt, BorderLayout.EAST);
			AorP.add(AMBox);
			AorP.add(PMBox);
			for (int i = 0; i < 28; i++) {
				DayPanel.add(DayLabel[i]);
			}

			this.setBackground(Color.WHITE);
			setSize(384, 300);
			setResizable(false);
			setVisible(true);
		}

		public Integer getLastDay(Integer year, Integer month) {
			Calendar cal = Calendar.getInstance();
			cal.set(year, month - 1, 1);

			int lastDay = cal.getActualMaximum(Calendar.DATE);

			return lastDay;
		}

		SetDialog() {

		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub\
			if (e.getSource() == SaveBt) {
				int chk = 0;
				try {
					String str = "";
					str += "AM:";
					if (AMBox.isSelected()) {
						str += "1\n";
						chk++;
					} else
						str += "0\n";
					str += "PM:";
					if (PMBox.isSelected()) {
						str += "1\n";
						chk++;
					} else
						str += "0\n";
					if (chk == 0) {
						JOptionPane.showMessageDialog(this, "오전 오후 중 하나는 체크해주세요.", "일치하지 않는 형식",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					str += "Day";
					chk = 0;
					int today = (getFirstWeek(year, month) + day - 1) % 7;
					for (int i = today + 7; i < today + 7 + 15; i++) {
						if (DayLabel[i].getBackground().equals(new Color(159, 216, 251))) {
							str += ":1";
							chk++;
						} else if (DayLabel[i].getBackground().equals(new Color(255, 182, 193)))
							str += ":0";
					}
					if (chk == 0) {
						JOptionPane.showMessageDialog(this, "요일 중 하나는 체크해주세요.", "일치하지 않는 형식",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					byte[] by = str.getBytes();
					OutputStream output = new FileOutputStream(".\\MySet.txt");
					output.write(by);
				} catch (Exception e1) {
					e1.getStackTrace();
				}

				this.dispose();
				TheEnd();
			} else if (e.getSource() == InfoBt) {
				JOptionPane.showMessageDialog(this, "추천 받을 수 있는 날짜는 2주 이내로 가능합니다.\n가능한 날짜 선택은 푸른색이 선택입니다.");
			} else {
				int today = (getFirstWeek(year, month) + day - 1) % 7;
				for (int i = today + 7; i < today + 7 + 15; i++) {
					if (e.getSource() == DayLabel[i]) {
						if (DayLabel[i].getBackground().equals(new Color(159, 216, 251))) {
							DayLabel[i].setBackground(new Color(255, 182, 193));
						} else if (DayLabel[i].getBackground().equals(new Color(255, 182, 193))) {
							DayLabel[i].setBackground(new Color(159, 216, 251));
						}
					}
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	void Recommend() {
		// e=℉ m=℃
		contents = new StringBuilder();
		try {
			URL url = new URL(urlPath);
			URLConnection con = (URLConnection) url.openConnection();
			InputStreamReader reader = new InputStreamReader(con.getInputStream(), "utf-8");
			BufferedReader buff = new BufferedReader(reader);
			while ((pageContents = buff.readLine()) != null) {
				contents.append(pageContents);
				contents.append("\r\n");
			}
			buff.close();
			try {
				OutputStream output = new FileOutputStream(".\\Weather.txt");
				String str = contents.toString();
				byte[] by = str.getBytes();
				output.write(by);
			} catch (Exception e) {
				e.getStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String page = null;
		try {
			// 바이트 단위로 파일읽기
			String filePath = ".\\Weather.txt"; // 대상 파일
			FileInputStream fileStream = null; // 파일 스트림

			fileStream = new FileInputStream(filePath);// 파일 스트림 생성
			// 버퍼 선언
			byte[] readBuffer = new byte[fileStream.available()];
			while (fileStream.read(readBuffer) != -1) {
			}
			page = new String(readBuffer);

			fileStream.close(); // 스트림 닫기
		} catch (Exception e) {
			e.getStackTrace();
		}
		// 요일 일
		cnt = 0;
		Pattern pattern = Pattern.compile("<span class=\"DailyContent--daypartDate--2A3Wi\">(.*?)</span>");
		Matcher matcher = pattern.matcher(page);
		while (matcher.find()) {
			String s = matcher.group(1);
			Day[cnt] = s + "일";
			cnt++;
		}
		keep = cnt;

		// 습도
		cnt = 0;
		pattern = Pattern
				.compile("<span data-testid=\"PercentageValue\" class=\"DetailsTable--value--1q_qD\">(.*?)%</span>");
		matcher = pattern.matcher(page);
		while (matcher.find()) {
			Hum[cnt] = Integer.parseInt(matcher.group(1));
			cnt++;
		}

		// 오전 기온
		cnt = 0;
		pattern = Pattern.compile("<span data-testid=\"TemperatureValue\" class=\"DetailsSummary--highTempValue--3Oteu\">(.*?)</span>");
		matcher = pattern.matcher(page);
		while (matcher.find()) {
			if (matcher.group(1).equals("--")) {
				cnt = 1;
				continue;
			}
			Scanner sc = new Scanner(matcher.group(1));
			sc.useDelimiter("°");
			Tem[cnt] = Integer.parseInt(sc.next());
			cnt += 2;
		}

		// 오후 기온
		if (Tem[0] == 0)
			cnt = 0;
		else
			cnt = 1;
		pattern = Pattern.compile(
				"<span data-testid=\"TemperatureValue\" class=\"DetailsSummary--lowTempValue--3H-7I\">(.*?)</span>");
		matcher = pattern.matcher(page);
		while (matcher.find()) {
			Scanner sc = new Scanner(matcher.group(1));
			sc.useDelimiter("°");
			Tem[cnt] = Integer.parseInt(sc.next());
			cnt += 2;
		}

		for (int i = 0; i < keep; i++) {
			if (Tem[i + 1] == 0)
				break;
		}

		for (int i = 0; i < keep; i++) {
			if (Tem[i + 1] == 0)
				break;
		}

		// 불쾌지수=1.8x기온–0.55x(1–습도)x(1.8x기온–26)+32
		for (int i = 0; i < keep; i++) {
			Discom[i] = (1.8f * (double) Tem[i] - 0.55f * (1.0f - (double) Hum[i] / 100.0f) * (1.8f * Tem[i] - 26.0f)
					+ 32.0f);
		}

		// 시간 기준
		pattern = Pattern.compile("<div class=\"DailyForecast--timestamp--22ExT\">(.*?)</div>");
		matcher = pattern.matcher(page);
		while (matcher.find()) {
			Time = matcher.group(1);
		}
		new SetDialog(Day);
	}

	void TheEnd() {
		int[] ChkInt = new int[20];
		String ChkStr[] = { "AM", "PM", "Day" };
		while (true) {
			try {
				File gradeFile = new File(".\\MySet.txt");

				Scanner inFile = new Scanner(gradeFile);

				try {
					inFile.hasNextLine();
					String line = inFile.nextLine();
					Scanner s = new Scanner(line);
					s.useDelimiter("\\s*:\\s*");
					if (!ChkStr[0].equals(s.next()))
						throw new Exception();
					ChkInt[0] = s.nextInt();
					if (ChkInt[0] != 1 && ChkInt[0] != 0) {
						NoMySet();
						continue;
					}
				} catch (Exception e) {
					NoMySet();
					continue;
				}

				try {
					inFile.hasNextLine();
					String line = inFile.nextLine();
					Scanner s = new Scanner(line);
					s.useDelimiter("\\s*:\\s*");
					if (!ChkStr[1].equals(s.next()))
						throw new Exception();
					ChkInt[1] = s.nextInt();
					if (ChkInt[1] != 1 && ChkInt[1] != 0) {
						NoMySet();
						continue;
					}
				} catch (Exception e) {
					NoMySet();
					continue;
				}

				try {
					inFile.hasNextLine();
					String line = inFile.nextLine();
					String[] st = line.split("[:]+");
					if (!ChkStr[2].equals(st[0])) {
						NoMySet();
						continue;
					}
					for (int i = 1; i < 16; i++) {
						ChkInt[i + 1] = Integer.parseInt(st[i]);
						if (ChkInt[i + 1] != 0 && ChkInt[i + 1] != 1) {
							NoMySet();
							continue;
						}
					}

				} catch (Exception e) {
					NoMySet();
					continue;
				}

			} catch (IOException e) {
				NoMySet();
				continue;
			}
			break;
		}

		NoDay = new int[31];
		if (ChkInt[0] == 0) {
			for (int i = 0; i < keep; i += 2) {
				if (keep % 2 == 1) {
					if (i == 0)
						i++;
					NoDay[i] = 1;
				} else {
					NoDay[i] = 1;
				}
			}
		}
		if (ChkInt[1] == 0) {
			for (int i = 0; i < keep; i += 2) {
				if (keep % 2 == 1) {
					NoDay[i] = 1;
				} else {
					if (i == 0)
						i++;
					NoDay[i] = 1;
				}
			}
		}
		for (int i = 0; i < 15; i++) {
			if (ChkInt[i + 2] == 0) {
				if (keep % 2 == 1) {
					if (i == 0)
						NoDay[i] = 1;
					else {
						NoDay[i * 2 - 1] = 1;
						NoDay[i * 2] = 1;
					}
				} else {
					NoDay[i * 2] = 1;
					NoDay[i * 2 + 1] = 1;
				}
			}
		}

		for (int i = 0; i < keep; i++) {
			if (Day[i].equals(Day[i + 1]))
				Day[i] += " 오전을";
			else
				Day[i] += " 오후를";
		}

		while (true) {
			double min = 5000;
			int num = 0, amount = 0;
			for (int i = 0; i < keep; i++) {
				if (NoDay[i] == 1) {
					continue;
				}
				if (min > Discom[i]) {
					min = Discom[i];
					num = i;
					amount++;
				}
			}

			if (amount == 0) {
				JOptionPane.showMessageDialog(this, "사용자가 원하는 날짜가 없습니다.", "메시지", JOptionPane.ERROR_MESSAGE);
				break;
			}

			int result = JOptionPane.showConfirmDialog(this, Day[num] + " 추천합니다.\n기온:" + Tem[num] + "℃   습도:" + Hum[num]
					+ "%   불쾌지수:" + (int) Discom[num] + "\n날짜에 만족하시나요?", Time, JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.NO_OPTION) {
				NoDay[num] = 1;
			} else if (result == JOptionPane.YES_OPTION) {
				if (keep % 2 == 1) {
					Calendar cal = Calendar.getInstance();
					year = cal.get(Calendar.YEAR);
					month = cal.get(Calendar.MONTH) + 1;
					day = cal.get(Calendar.DAY_OF_MONTH);
					int i = getFirstWeek(year, month) + day - 1 + (num+1) / 2;
					int n = i / 7, m = i % 7;
					CalLabel[n + 1][m].setBackground(new Color(206, 236, 251));
				} else {
					Calendar cal = Calendar.getInstance();
					year = cal.get(Calendar.YEAR);
					month = cal.get(Calendar.MONTH) + 1;
					day = cal.get(Calendar.DAY_OF_MONTH);
					int i = getFirstWeek(year, month) + day - 1 + num / 2;
					int n = i / 7, m = i % 7;
					CalLabel[n + 1][m].setBackground(new Color(206, 236, 251));
				}
				break;
			} else
				break;
		}
	}

	class MouseEventHandler implements MouseListener {
		int m, n;

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
				for (int i = 1; i < 9; i++) {
					for (int j = 0; j < 7; j++) {
						if (e.getSource() == CalLabel[i][j]) {
							pm.show(e.getComponent(), CalLabel[i][j].getWidth(), 0);
							m = i;
							n = j;
						}
					}
				}
			} else {
				if (e.getSource() == RecomBt) {
					Recommend();
				} else if (e.getSource() == pm_item) {
					Color c = JColorChooser.showDialog(null, "글자색 석택 화면", CalLabel[m][n].getBackground());
					if (c != null) {
						CalLabel[m][n].setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue()));
					}
				} else if (e.getSource() == pm_item1) {
					CalLabel[m][n].setBackground(CalColor[m][n]);
				}

			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

	}
}
