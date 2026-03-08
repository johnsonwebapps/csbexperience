function generateConfirmationNumber() {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
  let result = 'CSB-'
  for (let i = 0; i < 8; i++) {
    result += chars[Math.floor(Math.random() * chars.length)]
  }
  return result
}

const PRODUCTS_MAP = {
  'business-checking': 'Business Checking',
  'business-interest-checking': 'Business Interest Checking',
  'business-savings': 'Business Savings',
  'business-money-market': 'Business Money Market',
  'business-cd': 'Business Certificate of Deposit',
}

export default function Confirmation({ formData, onStartOver }) {
  const confirmationNumber = generateConfirmationNumber()
  const today = new Date().toLocaleDateString('en-US', {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
  })

  return (
    <div className="space-y-6">
      {/* Success header */}
      <div className="card text-center py-8">
        <div className="w-20 h-20 rounded-full bg-green-100 flex items-center justify-center mx-auto mb-4">
          <svg className="w-10 h-10 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M5 13l4 4L19 7" />
          </svg>
        </div>
        <h1 className="text-2xl font-bold text-gray-900 mb-2">Application Submitted!</h1>
        <p className="text-gray-600">
          Thank you, <strong>{formData.firstName}</strong>! Your business account application has been received.
        </p>
        <div className="inline-block mt-4 bg-csb-navy text-white rounded-xl px-6 py-3">
          <div className="text-xs text-blue-200 uppercase tracking-widest font-medium">Confirmation Number</div>
          <div className="text-2xl font-black tracking-wider mt-1">{confirmationNumber}</div>
        </div>
        <p className="text-sm text-gray-400 mt-3">{today}</p>
      </div>

      {/* Application summary */}
      <div className="card">
        <h2 className="font-bold text-csb-navy text-lg mb-4 border-b pb-3">Application Summary</h2>
        <div className="space-y-3 text-sm">
          <div className="flex justify-between py-1.5 border-b border-gray-50">
            <span className="text-gray-500">Business</span>
            <span className="font-semibold">{formData.businessName}</span>
          </div>
          <div className="flex justify-between py-1.5 border-b border-gray-50">
            <span className="text-gray-500">Account Type</span>
            <span className="font-semibold">{PRODUCTS_MAP[formData.selectedProduct]}</span>
          </div>
          <div className="flex justify-between py-1.5 border-b border-gray-50">
            <span className="text-gray-500">Primary Applicant</span>
            <span className="font-semibold">{formData.firstName} {formData.lastName}</span>
          </div>
          <div className="flex justify-between py-1.5">
            <span className="text-gray-500">Confirmation emailed to</span>
            <span className="font-semibold">{formData.email}</span>
          </div>
        </div>
      </div>

      {/* What happens next */}
      <div className="card">
        <h2 className="font-bold text-csb-navy text-lg mb-4 border-b pb-3">What Happens Next</h2>
        <div className="space-y-4">
          {[
            {
              step: '1',
              icon: '📧',
              title: 'Confirmation Email',
              desc: `A confirmation has been sent to ${formData.email}. Keep this for your records.`,
              time: 'Right now',
            },
            {
              step: '2',
              icon: '🔍',
              title: 'Application Review',
              desc: 'Our team will review your application and verify the information provided.',
              time: '1–2 business days',
            },
            {
              step: '3',
              icon: '📞',
              title: "We'll Be in Touch",
              desc: 'A CSB business banking specialist may contact you if additional documentation is needed.',
              time: '1–3 business days',
            },
            {
              step: '4',
              icon: '🏦',
              title: 'Account Open',
              desc: 'Once approved, your account will be opened and you\'ll receive your account details and debit card (if applicable).',
              time: '3–5 business days',
            },
          ].map((item) => (
            <div key={item.step} className="flex gap-4">
              <div className="w-10 h-10 rounded-full bg-csb-navy/10 flex items-center justify-center text-xl flex-shrink-0">
                {item.icon}
              </div>
              <div className="flex-1">
                <div className="font-semibold text-gray-800">{item.title}</div>
                <div className="text-sm text-gray-500 mt-0.5">{item.desc}</div>
                <div className="text-xs text-csb-green font-medium mt-1">{item.time}</div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Documents needed */}
      <div className="card">
        <h2 className="font-bold text-csb-navy text-lg mb-3 border-b pb-3">Documents You May Need to Provide</h2>
        <ul className="space-y-2 text-sm text-gray-600">
          {[
            'Articles of Incorporation / Organization (or equivalent formation documents)',
            'Operating Agreement or Bylaws',
            'Government-issued photo ID for all beneficial owners',
            'Certificate of Good Standing (for established businesses)',
            'Business license (if applicable to your industry)',
          ].map((doc, i) => (
            <li key={i} className="flex items-start gap-2">
              <span className="text-csb-navy font-bold flex-shrink-0">•</span>
              {doc}
            </li>
          ))}
        </ul>
        <p className="text-xs text-gray-400 mt-3">
          We may request these by secure email or you can bring them to any CSB branch location.
        </p>
      </div>

      {/* Visit a branch CTA */}
      <div className="rounded-xl overflow-hidden" style={{ background: 'linear-gradient(135deg, #003366 0%, #005599 100%)' }}>
        <div className="p-6 text-white text-center">
          <h3 className="font-bold text-xl mb-2">Have Questions?</h3>
          <p className="text-blue-100 text-sm mb-4">
            Our business banking team is here to help every step of the way.
          </p>
          <div className="flex flex-wrap justify-center gap-3">
            <a
              href="tel:1-888-418-5626"
              className="bg-white text-csb-navy font-bold px-5 py-2.5 rounded-lg text-sm hover:bg-blue-50 transition-colors"
            >
              📞 1-888-418-5626
            </a>
            <a
              href="#"
              className="bg-csb-gold text-csb-navy font-bold px-5 py-2.5 rounded-lg text-sm hover:opacity-90 transition-opacity"
            >
              📍 Find a Branch
            </a>
          </div>
        </div>
      </div>

      {/* Start over */}
      <div className="text-center">
        <button
          onClick={onStartOver}
          className="text-csb-navy font-semibold text-sm hover:underline"
        >
          ← Start a New Application
        </button>
      </div>
    </div>
  )
}
